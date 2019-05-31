package com.foolox.game.core.engin.game.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 29/05/2019
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NextPlayer {
    private String nextplayer ;
    private boolean takecard ;
}
