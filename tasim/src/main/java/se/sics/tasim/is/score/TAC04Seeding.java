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
 * $Id: TAC04Seeding.java,v 1.3 2008/01/07 17:47:06 nfi Exp $
 * -----------------------------------------------------------------
 *
 * TAC04Seeding
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson
 * Created : Sun Jul 11 20:29:48 2004
 * Updated : $Date: 2008/01/07 17:47:06 $
 *           $Revision: 1.3 $
 */
package se.sics.tasim.is.score;
import java.util.logging.Logger;
import se.sics.tasim.is.common.CompetitionParticipant;
import se.sics.tasim.is.common.ScoreMerger;

/**
 */
public class TAC04Seeding extends ScoreMerger {

  private static final Logger log =
    Logger.getLogger(TAC04Seeding.class.getName());

  private String GROUP_COLOR_A = "#ffd8d8";
  private String GROUP_COLOR_B = "#d5d6ff";
  private String GROUP_COLOR_C = "#ffffc0";
  private String GROUP_COLOR_D = "#d8ffd8";

  public TAC04Seeding() {
  }

  protected String getRankColor(CompetitionParticipant agent,
				int pos, int numberOfAgents) {
    if (pos > 24) {
      // Not qualified for the finals
      return null;
    }

    if (pos <= 3 || pos >= 22) {
      return GROUP_COLOR_A;
    } else if (pos <= 6 || pos >= 19) {
      return GROUP_COLOR_B;
    } else if (pos <= 9 || pos >= 16) {
      return GROUP_COLOR_C;
    } else {
      return GROUP_COLOR_D;
    }
  }

  protected void addPostInfo(StringBuffer page) {
    page.append("<b>The groups for the quarter-finals:</b><br>"
		+ "Group A (1,2,3,22,23,24) at tac3.sics.se,"
		+ "Group B (4,5,6,19,20,21) at tac4.sics.se,<br>"
		+ "Group C (7,8,9,16,17,18) at tac5.sics.se,"
		+ "Group D (10,11,12,13,14,15) at tac6.sics.se.");

//     page.append("<b>The groups for the semi-finals:</b><br>"
// 		+ "Group A: 1,2,3,22,23,24, "
// 		+ "Group B: 4,5,6,19,20,21,<br>"
// 		+ "Group C: 7,8,9,16,17,18, "
// 		+ "Group D: 10,11,12,13,14,15");
  }

} // TAC04Seeding
