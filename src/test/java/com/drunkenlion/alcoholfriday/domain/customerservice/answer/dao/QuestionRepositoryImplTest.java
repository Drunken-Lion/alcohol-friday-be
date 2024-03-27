package com.drunkenlion.alcoholfriday.domain.customerservice.answer.dao;

import com.drunkenlion.alcoholfriday.domain.auth.enumerated.ProviderType;
import com.drunkenlion.alcoholfriday.domain.customerservice.answer.entity.Answer;
import com.drunkenlion.alcoholfriday.domain.customerservice.question.dao.QuestionRepository;
import com.drunkenlion.alcoholfriday.domain.customerservice.question.entity.Question;
import com.drunkenlion.alcoholfriday.domain.customerservice.question.enumerated.QuestionStatus;
import com.drunkenlion.alcoholfriday.domain.member.dao.MemberRepository;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("[QuestionRepositoryImplTest] 문의사항 QueryDsl JPA Test")
@Transactional
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class QuestionRepositoryImplTest {
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private AnswerRepository answerRepository;
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private EntityManager em;

    @AfterEach
    @Transactional
    void afterEach() {
        questionRepository.deleteAll();
        answerRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("2번 회원이 작성한 일반 문의는 3개다")
    public void t1() {
        // test db가 mySql로 변경됨에 따라 auto_increment가 초기화되지 않으므로 시작 시 강제 초기화 진행
        em.createNativeQuery("ALTER TABLE member AUTO_INCREMENT = 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE question AUTO_INCREMENT = 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE answer AUTO_INCREMENT = 1").executeUpdate();

        Member 회원_슈퍼바이저1 = memberRepository.save(Member.builder()
                .email("supervisor1@example.com")
                .provider(ProviderType.KAKAO)
                .name("Supervisor1")
                .nickname("Supervisor1")
                .role(MemberRole.SUPER_VISOR)
                .phone(1012345678L)
                .certifyAt(LocalDate.now())
                .agreedToServiceUse(true)
                .agreedToServicePolicy(true)
                .agreedToServicePolicyUse(true)
                .build());

        Member 회원_일반회원1 = memberRepository.save(Member.builder()
                .email("member1@example.com")
                .provider(ProviderType.KAKAO)
                .name("Member1")
                .nickname("Member1")
                .role(MemberRole.MEMBER)
                .phone(1012345678L)
                .certifyAt(LocalDate.now())
                .agreedToServiceUse(true)
                .agreedToServicePolicy(true)
                .agreedToServicePolicyUse(true)
                .build());

        Member 회원_일반회원2 = memberRepository.save(Member.builder()
                .email("member2@example.com")
                .provider(ProviderType.KAKAO)
                .name("Member2")
                .nickname("Member2")
                .role(MemberRole.MEMBER)
                .phone(1012345678L)
                .certifyAt(LocalDate.now())
                .agreedToServiceUse(true)
                .agreedToServicePolicy(true)
                .agreedToServicePolicyUse(true)
                .build());

        Question 문의_일반1 = questionRepository.save(
                Question.builder()
                        .member(회원_일반회원1)
                        .title("일반 문의 제목 1")
                        .content("일반 문의 내용 1")
                        .status(QuestionStatus.COMPLETE)
                        .build());

        Question 문의_일반2 = questionRepository.save(
                Question.builder()
                        .member(회원_일반회원1)
                        .title("일반 문의 제목 2")
                        .content("일반 문의 내용 2")
                        .status(QuestionStatus.COMPLETE)
                        .build());

        Question 문의_일반3 = questionRepository.save(
                Question.builder()
                        .member(회원_일반회원1)
                        .title("일반 문의 제목 3")
                        .content("일반 문의 내용 3")
                        .status(QuestionStatus.COMPLETE)
                        .build());

        Question 문의_일반4 = questionRepository.save(
                Question.builder()
                        .member(회원_일반회원2)
                        .title("일반 문의 제목 4")
                        .content("일반 문의 내용 4")
                        .status(QuestionStatus.COMPLETE)
                        .build());

        Question 문의_일반5 = questionRepository.save(
                Question.builder()
                        .member(회원_일반회원2)
                        .title("일반 문의 제목 5")
                        .content("일반 문의 내용 5")
                        .status(QuestionStatus.COMPLETE)
                        .build());

        Answer 문의_답변1 = answerRepository.save(
                Answer.builder()
                        .member(회원_슈퍼바이저1)
                        .content("일반 문의 답변 1")
                        .build());
        문의_답변1.addQuestion(문의_일반1);
        answerRepository.save(문의_답변1);

        Answer 문의_답변2 = answerRepository.save(
                Answer.builder()
                        .member(회원_슈퍼바이저1)
                        .content("일반 문의 답변 2")
                        .build());
        문의_답변2.addQuestion(문의_일반1);
        answerRepository.save(문의_답변2);

        Answer 문의_답변3 = answerRepository.save(
                Answer.builder()
                        .member(회원_슈퍼바이저1)
                        .content("일반 문의 답변 3")
                        .build());
        문의_답변3.addQuestion(문의_일반1);
        answerRepository.save(문의_답변3);

        Answer 문의_답변4 = answerRepository.save(
                Answer.builder()
                        .member(회원_슈퍼바이저1)
                        .content("일반 문의 답변 4")
                        .build());
        문의_답변4.addQuestion(문의_일반1);
        answerRepository.save(문의_답변4);

        Answer 문의_답변5 = answerRepository.save(
                Answer.builder()
                        .member(회원_슈퍼바이저1)
                        .content("일반 문의 답변 5")
                        .build());
        문의_답변5.addQuestion(문의_일반1);
        answerRepository.save(문의_답변5);

        Member member = Member
                .builder()
                .id(회원_일반회원1.getId())
                .build();

        Pageable pageable = PageRequest.ofSize(10);

        Page<Question> findAll = questionRepository.findMember(member, pageable);

        assertThat(findAll).isNotNull();
        assertThat(findAll.getContent()).isInstanceOf(List.class);
        assertThat(findAll.getContent().size()).isEqualTo(3);
    }

    @Test
    @DisplayName("2번 회원이 작성한 일반 문의 전체 조회 후 1번 게시물 조회")
    public void t2() {
        // test db가 mySql로 변경됨에 따라 auto_increment가 초기화되지 않으므로 시작 시 강제 초기화 진행
        em.createNativeQuery("ALTER TABLE member AUTO_INCREMENT = 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE question AUTO_INCREMENT = 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE answer AUTO_INCREMENT = 1").executeUpdate();

        Member 회원_슈퍼바이저1 = memberRepository.save(Member.builder()
                .email("supervisor1@example.com")
                .provider(ProviderType.KAKAO)
                .name("Supervisor1")
                .nickname("Supervisor1")
                .role(MemberRole.SUPER_VISOR)
                .phone(1012345678L)
                .certifyAt(LocalDate.now())
                .agreedToServiceUse(true)
                .agreedToServicePolicy(true)
                .agreedToServicePolicyUse(true)
                .build());

        Member 회원_일반회원1 = memberRepository.save(Member.builder()
                .email("member1@example.com")
                .provider(ProviderType.KAKAO)
                .name("Member1")
                .nickname("Member1")
                .role(MemberRole.MEMBER)
                .phone(1012345678L)
                .certifyAt(LocalDate.now())
                .agreedToServiceUse(true)
                .agreedToServicePolicy(true)
                .agreedToServicePolicyUse(true)
                .build());

        Member 회원_일반회원2 = memberRepository.save(Member.builder()
                .email("member2@example.com")
                .provider(ProviderType.KAKAO)
                .name("Member2")
                .nickname("Member2")
                .role(MemberRole.MEMBER)
                .phone(1012345678L)
                .certifyAt(LocalDate.now())
                .agreedToServiceUse(true)
                .agreedToServicePolicy(true)
                .agreedToServicePolicyUse(true)
                .build());

        Question 문의_일반1 = questionRepository.save(
                Question.builder()
                        .member(회원_일반회원1)
                        .title("일반 문의 제목 1")
                        .content("일반 문의 내용 1")
                        .status(QuestionStatus.COMPLETE)
                        .build());

        Question 문의_일반2 = questionRepository.save(
                Question.builder()
                        .member(회원_일반회원1)
                        .title("일반 문의 제목 2")
                        .content("일반 문의 내용 2")
                        .status(QuestionStatus.COMPLETE)
                        .build());

        Question 문의_일반3 = questionRepository.save(
                Question.builder()
                        .member(회원_일반회원1)
                        .title("일반 문의 제목 3")
                        .content("일반 문의 내용 3")
                        .status(QuestionStatus.COMPLETE)
                        .build());

        Question 문의_일반4 = questionRepository.save(
                Question.builder()
                        .member(회원_일반회원2)
                        .title("일반 문의 제목 4")
                        .content("일반 문의 내용 4")
                        .status(QuestionStatus.COMPLETE)
                        .build());

        Question 문의_일반5 = questionRepository.save(
                Question.builder()
                        .member(회원_일반회원2)
                        .title("일반 문의 제목 5")
                        .content("일반 문의 내용 5")
                        .status(QuestionStatus.COMPLETE)
                        .build());

        Answer 문의_답변1 = answerRepository.save(
                Answer.builder()
                        .member(회원_슈퍼바이저1)
                        .content("일반 문의 답변 1")
                        .build());
        문의_답변1.addQuestion(문의_일반1);
        answerRepository.save(문의_답변1);

        Answer 문의_답변2 = answerRepository.save(
                Answer.builder()
                        .member(회원_슈퍼바이저1)
                        .content("일반 문의 답변 2")
                        .build());
        문의_답변2.addQuestion(문의_일반1);
        answerRepository.save(문의_답변2);

        Answer 문의_답변3 = answerRepository.save(
                Answer.builder()
                        .member(회원_슈퍼바이저1)
                        .content("일반 문의 답변 3")
                        .build());
        문의_답변3.addQuestion(문의_일반1);
        answerRepository.save(문의_답변3);

        Answer 문의_답변4 = answerRepository.save(
                Answer.builder()
                        .member(회원_슈퍼바이저1)
                        .content("일반 문의 답변 4")
                        .build());
        문의_답변4.addQuestion(문의_일반1);
        answerRepository.save(문의_답변4);

        Answer 문의_답변5 = answerRepository.save(
                Answer.builder()
                        .member(회원_슈퍼바이저1)
                        .content("일반 문의 답변 5")
                        .build());
        문의_답변5.addQuestion(문의_일반1);
        answerRepository.save(문의_답변5);

        Member member = Member
                .builder()
                .id(회원_일반회원1.getId())
                .build();

        Pageable pageable = PageRequest.ofSize(10);

        Page<Question> findAll = questionRepository.findMember(member, pageable);

        assertThat(findAll).isNotNull();
        assertThat(findAll.getContent()).isInstanceOf(List.class);
        assertThat(findAll.getContent().size()).isEqualTo(3);
        assertThat(findAll.getContent().get(0).getId()).isEqualTo(1);
        assertThat(findAll.getContent().get(0).getTitle()).isEqualTo("일반 문의 제목 1");
        assertThat(findAll.getContent().get(0).getContent()).isEqualTo("일반 문의 내용 1");
        assertThat(findAll.getContent().get(0).getStatus()).isEqualTo(QuestionStatus.COMPLETE);
        assertThat(findAll.getContent().get(0).getMember().getId()).isEqualTo(member.getId());
        assertThat(findAll.getContent().get(0).getCreatedAt()).isNotNull();

        assertThat(findAll.getContent().get(0).getAnswers().size()).isEqualTo(5);
        assertThat(findAll.getContent().get(0).getAnswers().get(0).getId()).isEqualTo(1);
        assertThat(findAll.getContent().get(0).getAnswers().get(0).getContent()).isEqualTo("일반 문의 답변 1");
        assertThat(findAll.getContent().get(0).getAnswers().get(0).getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("신규 문의사항을 생성하면 id값이 발행된다.")
    public void t3() {
        // test db가 mySql로 변경됨에 따라 auto_increment가 초기화되지 않으므로 시작 시 강제 초기화 진행
        em.createNativeQuery("ALTER TABLE member AUTO_INCREMENT = 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE question AUTO_INCREMENT = 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE answer AUTO_INCREMENT = 1").executeUpdate();

        Member 회원_슈퍼바이저1 = memberRepository.save(Member.builder()
                .email("supervisor1@example.com")
                .provider(ProviderType.KAKAO)
                .name("Supervisor1")
                .nickname("Supervisor1")
                .role(MemberRole.SUPER_VISOR)
                .phone(1012345678L)
                .certifyAt(LocalDate.now())
                .agreedToServiceUse(true)
                .agreedToServicePolicy(true)
                .agreedToServicePolicyUse(true)
                .build());

        Member 회원_일반회원1 = memberRepository.save(Member.builder()
                .email("member1@example.com")
                .provider(ProviderType.KAKAO)
                .name("Member1")
                .nickname("Member1")
                .role(MemberRole.MEMBER)
                .phone(1012345678L)
                .certifyAt(LocalDate.now())
                .agreedToServiceUse(true)
                .agreedToServicePolicy(true)
                .agreedToServicePolicyUse(true)
                .build());

        Member 회원_일반회원2 = memberRepository.save(Member.builder()
                .email("member2@example.com")
                .provider(ProviderType.KAKAO)
                .name("Member2")
                .nickname("Member2")
                .role(MemberRole.MEMBER)
                .phone(1012345678L)
                .certifyAt(LocalDate.now())
                .agreedToServiceUse(true)
                .agreedToServicePolicy(true)
                .agreedToServicePolicyUse(true)
                .build());

        Question 문의_일반1 = questionRepository.save(
                Question.builder()
                        .member(회원_일반회원1)
                        .title("일반 문의 제목 1")
                        .content("일반 문의 내용 1")
                        .status(QuestionStatus.COMPLETE)
                        .build());

        Question 문의_일반2 = questionRepository.save(
                Question.builder()
                        .member(회원_일반회원1)
                        .title("일반 문의 제목 2")
                        .content("일반 문의 내용 2")
                        .status(QuestionStatus.COMPLETE)
                        .build());

        Question 문의_일반3 = questionRepository.save(
                Question.builder()
                        .member(회원_일반회원1)
                        .title("일반 문의 제목 3")
                        .content("일반 문의 내용 3")
                        .status(QuestionStatus.COMPLETE)
                        .build());

        Question 문의_일반4 = questionRepository.save(
                Question.builder()
                        .member(회원_일반회원2)
                        .title("일반 문의 제목 4")
                        .content("일반 문의 내용 4")
                        .status(QuestionStatus.COMPLETE)
                        .build());

        Question 문의_일반5 = questionRepository.save(
                Question.builder()
                        .member(회원_일반회원2)
                        .title("일반 문의 제목 5")
                        .content("일반 문의 내용 5")
                        .status(QuestionStatus.COMPLETE)
                        .build());

        Answer 문의_답변1 = answerRepository.save(
                Answer.builder()
                        .member(회원_슈퍼바이저1)
                        .content("일반 문의 답변 1")
                        .build());
        문의_답변1.addQuestion(문의_일반1);
        answerRepository.save(문의_답변1);

        Answer 문의_답변2 = answerRepository.save(
                Answer.builder()
                        .member(회원_슈퍼바이저1)
                        .content("일반 문의 답변 2")
                        .build());
        문의_답변2.addQuestion(문의_일반1);
        answerRepository.save(문의_답변2);

        Answer 문의_답변3 = answerRepository.save(
                Answer.builder()
                        .member(회원_슈퍼바이저1)
                        .content("일반 문의 답변 3")
                        .build());
        문의_답변3.addQuestion(문의_일반1);
        answerRepository.save(문의_답변3);

        Answer 문의_답변4 = answerRepository.save(
                Answer.builder()
                        .member(회원_슈퍼바이저1)
                        .content("일반 문의 답변 4")
                        .build());
        문의_답변4.addQuestion(문의_일반1);
        answerRepository.save(문의_답변4);

        Answer 문의_답변5 = answerRepository.save(
                Answer.builder()
                        .member(회원_슈퍼바이저1)
                        .content("일반 문의 답변 5")
                        .build());
        문의_답변5.addQuestion(문의_일반1);
        answerRepository.save(문의_답변5);

        Member member = Member.builder()
                .id(1L)
                .build();

        String title = "삽입 테스트 제목";
        String content = "삽입 테스트 내용";
        QuestionStatus status = QuestionStatus.INCOMPLETE;

        Question saveQuestion = questionRepository.save(Question.builder()
                .member(member)
                .title(title)
                .content(content)
                .status(status)
                .build());

        assertThat(saveQuestion.getId()).isNotNull();
        assertThat(saveQuestion.getCreatedAt()).isNotNull();
        assertThat(saveQuestion.getTitle()).isEqualTo(title);
        assertThat(saveQuestion.getContent()).isEqualTo(content);
        assertThat(saveQuestion.getStatus()).isEqualTo(status);
    }
}
