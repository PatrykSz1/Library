package pl.masters.testmail.model.message;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Message {

    private String to;
    private String topic;
    private String text;
}
