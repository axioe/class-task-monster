package com.classmonster.config;

import com.classmonster.domain.Assignment;
import com.classmonster.domain.Classroom;
import com.classmonster.domain.ExpressionMission;
import com.classmonster.domain.ExpressionOption;
import com.classmonster.domain.FoodItem;
import com.classmonster.domain.FoodType;
import com.classmonster.domain.GrowthNews;
import com.classmonster.domain.User;
import com.classmonster.repository.AssignmentRepository;
import com.classmonster.repository.ClassroomRepository;
import com.classmonster.repository.ExpressionMissionRepository;
import com.classmonster.repository.FoodItemRepository;
import com.classmonster.repository.GrowthNewsRepository;
import com.classmonster.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 개발용 샘플 데이터 (app.seed-data=true 일 때만 실행).
 * 계정 정보는 README 의 "샘플 계정" 참고.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.seed-data", havingValue = "true")
public class DevDataInitializer implements ApplicationRunner {

    private static final String TEACHER_PASSWORD = "teacher1234";
    private static final String STUDENT_PASSWORD = "student1234";

    private final UserRepository userRepository;
    private final ClassroomRepository classroomRepository;
    private final AssignmentRepository assignmentRepository;
    private final FoodItemRepository foodItemRepository;
    private final ExpressionMissionRepository missionRepository;
    private final GrowthNewsRepository growthNewsRepository;
    private final PasswordEncoder passwordEncoder;
    private final Clock clock;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (userRepository.existsByLoginId("teacher")) {
            return;
        }

        User teacher = userRepository.save(User.teacher("teacher", passwordEncoder.encode(TEACHER_PASSWORD), "김선생"));
        Classroom classroom = classroomRepository.save(new Classroom("5학년 2반", "몽글이", teacher));
        growthNewsRepository.save(new GrowthNews(classroom, "우리 반에 몽글이의 알이 도착했어요! 함께 키워 볼까요?"));
        classroom.addEnergy(18);
        growthNewsRepository.save(new GrowthNews(classroom, "알이 깨어났어요! 아기 몽글이와 인사해 볼까요?"));

        String studentPassword = passwordEncoder.encode(STUDENT_PASSWORD);
        User jiwoo = userRepository.save(User.student("jiwoo", studentPassword, "지우", classroom));
        User minjun = userRepository.save(User.student("minjun", studentPassword, "민준", classroom));
        userRepository.save(User.student("seoyeon", studentPassword, "서연", classroom));

        LocalDateTime now = LocalDateTime.now(clock);

        Assignment words = new Assignment(classroom, jiwoo, "단어 3개 익히기", "apple, banana, orange 를 소리 내어 읽고 써 보기");
        words.submit("세 단어를 다섯 번씩 써 봤어요.", now.minusHours(2));
        words.approve("발음이 아주 정확했어요!", now.minusHours(1));
        words.markFoodGranted();
        assignmentRepository.save(words);
        assignmentRepository.save(new Assignment(classroom, jiwoo, "문장 2개 만들기", "I like ~ 를 사용해 문장 2개 만들기"));
        assignmentRepository.save(new Assignment(classroom, minjun, "인사 표현 연습", "Good morning / Nice to meet you 대화 연습"));

        foodItemRepository.save(new FoodItem(jiwoo, FoodType.STRAWBERRY));
        foodItemRepository.save(new FoodItem(jiwoo, FoodType.GRAPE));

        ExpressionMission mission = new ExpressionMission(classroom, LocalDate.now(clock), "몽글이에게 먹이를 건네줘");
        mission.replaceOptions(List.of(
                new ExpressionOption("Here you are!", true),
                new ExpressionOption("Good night!", false),
                new ExpressionOption("I'm sorry.", false)));
        missionRepository.save(mission);

        log.info("[seed] 샘플 데이터 생성 완료 - 교사: teacher / 학생: jiwoo, minjun, seoyeon");
    }
}
