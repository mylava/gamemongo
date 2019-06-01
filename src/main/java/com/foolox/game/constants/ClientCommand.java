package com.foolox.game.constants;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 31/05/2019
 */
@Data
@NoArgsConstructor
public class ClientCommand {
    private String command ;
    private String data ;
    private String token ;
}
