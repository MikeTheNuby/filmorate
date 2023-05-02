package ru.yandex.practicum.filmorate.model;

import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class Friend {

    private final long idUser;
    private final long idFriend;
    private long idFriendship;

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("user_id", idUser);
        values.put("friend_id", idFriend);
        return values;
    }
}
