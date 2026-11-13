package com.classmonster.service;

import com.classmonster.domain.Classroom;
import com.classmonster.domain.GrowthNews;
import com.classmonster.domain.GrowthStage;
import com.classmonster.dto.news.GrowthNewsResponse;
import com.classmonster.repository.GrowthNewsRepository;
import com.classmonster.util.KoreanJosa;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 공동 몬스터 성장과 "우리 반 성장 소식"을 담당한다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GrowthService {

    private final GrowthNewsRepository growthNewsRepository;

    /**
     * 몬스터에게 에너지를 더하고, 단계가 바뀌면 성장 소식을 남긴다.
     *
     * @return 새로 생긴 성장 소식 (단계가 그대로면 null)
     */
    @Transactional
    public GrowthNews grow(Classroom classroom, int energy) {
        boolean stageChanged = classroom.addEnergy(energy);
        if (!stageChanged) {
            return null;
        }
        return addNews(classroom, stageUpMessage(classroom.getMonsterName(), classroom.getStage()));
    }

    /** 반이 처음 만들어졌을 때의 소식 */
    @Transactional
    public void announceArrival(Classroom classroom) {
        addNews(classroom, "우리 반에 " + classroom.getMonsterName() + "의 알이 도착했어요! 함께 키워 볼까요?");
    }

    @Transactional
    public GrowthNews addNews(Classroom classroom, String message) {
        return growthNewsRepository.save(new GrowthNews(classroom, message));
    }

    public List<GrowthNewsResponse> recentNews(Classroom classroom) {
        return growthNewsRepository.findTop5ByClassroomOrderByIdDesc(classroom).stream()
                .map(GrowthNewsResponse::from)
                .toList();
    }

    public List<GrowthNewsResponse> allNews(Classroom classroom) {
        return growthNewsRepository.findAllByClassroomOrderByIdDesc(classroom).stream()
                .map(GrowthNewsResponse::from)
                .toList();
    }

    private String stageUpMessage(String monsterName, GrowthStage stage) {
        return switch (stage) {
            case BABY -> "알이 깨어났어요! 아기 " + KoreanJosa.with(monsterName) + " 인사해 볼까요?";
            case GROWN -> "함께 모은 먹이로 " + monsterName + "에게 작은 날개가 생겼어요!";
            case EGG -> KoreanJosa.subject(monsterName) + " 알 속에서 꿈틀거려요.";
        };
    }
}
