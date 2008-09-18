/**
 * Copyright (c) 2001-2008, Swedish Institute of Computer Science
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the Institute nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * This file is part of the TAC Supply Chain Management Simulator.
 *
 * $Id: TAC05Seeding.java,v 1.4 2008/01/07 17:47:06 nfi Exp $
 * -----------------------------------------------------------------
 *
 * TAC05Seeding
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson
 * Created : Mon Jul 11 09:29:48 2005
 * Updated : $Date: 2008/01/07 17:47:06 $
 *           $Revision: 1.4 $
 */
package se.sics.tasim.is.score;
import java.util.logging.Logger;
import se.sics.tasim.is.common.CompetitionParticipant;
import se.sics.tasim.is.common.ScoreMerger;

/**
 */
public class TAC05Seeding extends MinAvgZeroScoreMerger {

  private static final Logger log =
    Logger.getLogger(TAC05Seeding.class.getName());

  private static final String GROUP_COLOR_A = "#ffd8d8";
  private static final String GROUP_COLOR_B = "#d5d6ff";
  private static final String GROUP_COLOR_C = "#ffffc0";
  private static final String GROUP_COLOR_D = "#d8ffd8";

  private static final String[] GROUPS = {
    GROUP_COLOR_A, //  1
    GROUP_COLOR_B, //  2
    GROUP_COLOR_C, //  3
    GROUP_COLOR_D, //  4
    GROUP_COLOR_D, //  5
    GROUP_COLOR_C, //  6
    GROUP_COLOR_B, //  7
    GROUP_COLOR_A, //  8
    GROUP_COLOR_A, //  9
    GROUP_COLOR_B, // 10
    GROUP_COLOR_C, // 11
    GROUP_COLOR_D, // 12
    GROUP_COLOR_D, // 13
    GROUP_COLOR_C, // 14
    GROUP_COLOR_B, // 15
    GROUP_COLOR_A, // 16
    GROUP_COLOR_A, // 17
    GROUP_COLOR_B, // 18
    GROUP_COLOR_C, // 19
    GROUP_COLOR_D, // 20
    GROUP_COLOR_D, // 21
    GROUP_COLOR_C, // 22
    GROUP_COLOR_B, // 23
    GROUP_COLOR_A  // 24
  };

  public TAC05Seeding() {
    setShowingAverageScoreWhenWeighted(false);
  }

  protected String[] getGroupColors() {
    return GROUPS;
  }

  protected String getRankColor(CompetitionParticipant agent,
				int pos, int numberOfAgents) {
    String[] groups = getGroupColors();
    if (groups == null || (pos < 1) || pos > groups.length) {
      // Not qualified for the finals
      return null;
    }
    return groups[pos - 1];
  }

  protected void addPostInfo(StringBuffer page) {
    super.addPostInfo(page);
    page.append("<p><b>The groups for the quarter-finals:</b><br>"
		+ "Group A (1,8, 9,16,17,24) at tac3.sics.se,"
		+ "Group B (2,7,10,15,18,23) at tac4.sics.se,<br>"
		+ "Group C (3,6,11,14,19,22) at tac5.sics.se,"
		+ "Group D (4,5,12,13,20,21) at tac6.sics.se.");
  }

} // TAC05Seeding
