package com.classmonster.dto.classroom;

import com.classmonster.domain.Classroom;
import com.classmonster.dto.monster.MonsterResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class ClassroomDtos {

    private ClassroomDtos() {
    }

    /** 반 생성/수정 */
    public record ClassroomRequest(
            @NotBlank @Size(max = 50) String name,
            @NotBlank @Size(max = 30) String monsterName) {
    }

    public record ClassroomResponse(Long id, String name, long studentCount, MonsterResponse monster) {
        public static ClassroomResponse of(Classroom classroom, long studentCount) {
            return new ClassroomResponse(classroom.getId(), classroom.getName(), studentCount,
                    MonsterResponse.from(classroom));
        }
    }
}
