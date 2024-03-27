package com.drunkenlion.alcoholfriday.domain.payment.application;

import com.drunkenlion.alcoholfriday.domain.cart.application.CartService;
import com.drunkenlion.alcoholfriday.domain.cart.dto.request.DeleteCartRequest;
import com.drunkenlion.alcoholfriday.domain.item.entity.ItemProduct;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.order.application.OrderService;
import com.drunkenlion.alcoholfriday.domain.order.dao.OrderRepository;
import com.drunkenlion.alcoholfriday.domain.order.entity.Order;
import com.drunkenlion.alcoholfriday.domain.order.entity.OrderDetail;
import com.drunkenlion.alcoholfriday.domain.order.util.OrderValidator;
import com.drunkenlion.alcoholfriday.domain.payment.dao.PaymentRepository;
import com.drunkenlion.alcoholfriday.domain.payment.dto.request.TossPaymentsReq;
import com.drunkenlion.alcoholfriday.domain.payment.dto.response.TossApiResponse;
import com.drunkenlion.alcoholfriday.domain.payment.entity.Payment;
import com.drunkenlion.alcoholfriday.domain.payment.util.PaymentValidator;
import com.drunkenlion.alcoholfriday.domain.product.dao.ProductRepository;
import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import com.drunkenlion.alcoholfriday.global.common.enumerated.OrderStatus;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.common.util.RoleValidator;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentServiceImpl implements PaymentService {
    private final CartService cartService;
    private final OrderService orderService;
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Value("${custom.tossPayments.widget.secretKey}")
    private String tossPaymentsWidgetSecretKey;

    /**
     * 결제 금액 유효성 확인
     */
    @Override
    public void validatePaymentAmount(String orderNo, BigDecimal amount) {
        Order order = orderService.getOrder(orderNo);

        PaymentValidator.orderStatusIsOrderReceived(order);
        PaymentValidator.checkTotalPrice(order, amount);
    }

    /**
     * 결제 성공 시 Payment 저장
     */
    @Override
    @Transactional
    public void saveSuccessPayment(TossPaymentsReq tossPaymentsReq, Order order, Member member) {
        // Order 상태 정보 업데이트
        order.updateOrderStatus(OrderStatus.PAYMENT_COMPLETED);
        orderRepository.save(order);

        Payment payment = TossPaymentsReq.toEntity(tossPaymentsReq, member, order);
        paymentRepository.save(payment);
    }

    /**
     * 결제 성공 시 장바구니에서 해당 주문 아이템 제거
     */
    @Override
    @Transactional
    public void deletedCartItems(List<OrderDetail> orderDetails, Member member) {
        List<DeleteCartRequest> deleteCartRequests = orderDetails.stream()
                .map(orderDetail -> DeleteCartRequest.of(orderDetail.getItem().getId()))
                .toList();

        cartService.deleteCartList(deleteCartRequests, member);
    }

    /**
     * 결제 관련 동작 중 필요한 item, item_product, product의 삭제 확인
     */
    @Override
    public void checkDeletedData(List<OrderDetail> orderDetails) {
        // 주문 상세의 item, item_product, product 데이터 중 하나라도 삭제된 데이터가 있다면 결제 승인, 취소, 환불 불가
        for (OrderDetail detail : orderDetails) {
            if (detail.getItem().getDeletedAt() != null) {
                throw BusinessException.builder()
                        .response(HttpResponse.Fail.EXIST_DELETED_DATA)
                        .build();
            }
            for (ItemProduct ip : detail.getItem().getItemProducts()) {
                if (ip.getDeletedAt() != null || ip.getProduct().getDeletedAt() != null) {
                    throw BusinessException.builder()
                            .response(HttpResponse.Fail.EXIST_DELETED_DATA)
                            .build();
                }
            }
        }
    }

    /**
     * 결제 취소 가능 여부 체크
     */
    @Override
    public void checkCancelPayment(Order order, List<OrderDetail> orderDetails, Member member) {
        RoleValidator.validateAdminOrStoreManager(member);
        OrderValidator.checkOrderStatusAbleCancelComplete(order);
        checkDeletedData(orderDetails);
    }

    /**
     * 결제 취소 성공 시 Payment 저장
     */
    @Override
    @Transactional
    public void saveCancelSuccessPayment(TossPaymentsReq tossPaymentsReq, Order order, List<OrderDetail> orderDetails, Member member) {
        order.updateOrderStatus(OrderStatus.CANCEL_COMPLETED);
        orderRepository.save(order);

        List<Product> productList = new ArrayList<>();
        for (OrderDetail detail : orderDetails) {
            List<ItemProduct> itemProducts = detail.getItem().getItemProducts();
            itemProducts.forEach(itemProduct -> {
                plusProductQuantity(detail.getQuantity(), itemProduct);
                productList.add(itemProduct.getProduct());
            });
        }
        productRepository.saveAll(productList);

        Payment payment = TossPaymentsReq.toEntity(tossPaymentsReq, member, order);
        paymentRepository.save(payment);
    }

    private void plusProductQuantity(Long orderItemRequestQuantity, ItemProduct itemProduct) {
        Product product = itemProduct.getProduct();
        Long productQuantity = product.getQuantity();

        Long itemQuantity = itemProduct.getQuantity(); // item에 따른 개수
        Long plusProductQuantity = orderItemRequestQuantity * itemQuantity;

        product.updateQuantity(productQuantity + plusProductQuantity);
    }

    @Override
    public TossApiResponse getTossPaymentsResult(URL url, JSONObject obj, JSONParser parser) throws Exception {
        String widgetSecretKey = tossPaymentsWidgetSecretKey;
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((widgetSecretKey + ":").getBytes("UTF-8"));
        String authorizations = "Basic " + new String(encodedBytes, 0, encodedBytes.length);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", authorizations);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(obj.toString().getBytes("UTF-8"));

        int code = connection.getResponseCode();
        boolean isSuccess = code == 200 ? true : false;

        InputStream responseStream = isSuccess ? connection.getInputStream() : connection.getErrorStream();

        Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8);
        JSONObject jsonObject = (JSONObject) parser.parse(reader);
        responseStream.close();

        return TossApiResponse.builder()
                .isSuccess(isSuccess)
                .code(code)
                .jsonObject(jsonObject)
                .build();
    }
}
