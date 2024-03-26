package com.drunkenlion.alcoholfriday.domain.payment.api;

import com.drunkenlion.alcoholfriday.domain.payment.application.PaymentService;
import com.drunkenlion.alcoholfriday.domain.payment.dto.request.TossPaymentsReq;
import com.drunkenlion.alcoholfriday.global.security.auth.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/payments")
@Tag(name = "v1-payment", description = "결제 관련 API")
@SecurityRequirement(name = "bearerAuth")
public class PaymentController {
    private final PaymentService paymentService;

    @Value("${custom.tossPayments.widget.secretKey}")
    private String tossPaymentsWidgetSecretKey;

    @Operation(summary = "결제",
            description = "클라이언트에서 결제 정보를 전달 받고 서버에서 한 번 더 검사 후 토스페이먼츠로 최종 결제 승인 요청" +
                    " / jsonBody의 경우 orderNo, amount(배송비 포함 주문 총 금액), paymentKey, direct를 주시면 됩니다. " +
                    "- 즉시 주문일 경우 direct : true, 장바구니 주문일 경우 direct : false 로 보내주시길 바랍니다.")
    @PostMapping("confirm")
    public ResponseEntity<JSONObject> confirmPayment(@RequestBody String jsonBody,
                                                     @AuthenticationPrincipal UserPrincipal userPrincipal) throws Exception {

        JSONParser parser = new JSONParser();
        String orderNo;
        String amount;
        String paymentKey;
        boolean direct;
        try {
            JSONObject requestData = (JSONObject) parser.parse(jsonBody);
            paymentKey = (String) requestData.get("paymentKey");
            orderNo = (String) requestData.get("orderNo");
            amount = (String) requestData.get("amount");
            String directString = (String) requestData.get("direct");
            direct = Boolean.parseBoolean(directString); // true: 즉시 구매, false: 장바구니 구매
        } catch (ParseException e) {
            throw new RuntimeException(e);
        };

        // 결제 금액 유효성 확인
        paymentService.validatePaymentAmount(orderNo, new BigDecimal(amount));

        JSONObject obj = new JSONObject();
        obj.put("orderId", orderNo);
        obj.put("amount", amount);
        obj.put("paymentKey", paymentKey);

        String widgetSecretKey = tossPaymentsWidgetSecretKey;
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((widgetSecretKey + ":").getBytes("UTF-8"));
        String authorizations = "Basic " + new String(encodedBytes, 0, encodedBytes.length);

        URL url = new URL("https://api.tosspayments.com/v1/payments/confirm");
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

        // 결제 성공 시
        if (isSuccess) {
            paymentService.saveSuccessPayment(TossPaymentsReq.of(orderNo, paymentKey, jsonObject));
            if (!direct) paymentService.deletedCartItems(orderNo); // 장바구니 구매 시
        }

        return ResponseEntity.status(code).body(jsonObject);
    }
}
