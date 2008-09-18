/**
 * TAC Supply Chain Management Simulator
 * http://www.sics.se/tac/    tac-dev@sics.se
 *
 * Copyright (c) 2001-2005 SICS AB. All rights reserved.
 *
 * SICS grants you the right to use, modify, and redistribute this
 * software for noncommercial purposes, on the conditions that you:
 * (1) retain the original headers, including the copyright notice and
 * this text, (2) clearly document the difference between any derived
 * software and the original, and (3) acknowledge your use of this
 * software in pertaining publications and reports.  SICS provides
 * this software "as is", without any warranty of any kind.  IN NO
 * EVENT SHALL SICS BE LIABLE FOR ANY DIRECT, SPECIAL OR INDIRECT,
 * PUNITIVE, INCIDENTAL OR CONSEQUENTIAL LOSSES OR DAMAGES ARISING OUT
 * OF THE USE OF THE SOFTWARE.
 *
 * -----------------------------------------------------------------
 *
 * DefaultScoreGenerator
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson
 * Created : Tue Aug 05 13:35:28 2003
 * Updated : $Date: 2008-04-11 15:51:54 -0500 (Fri, 11 Apr 2008) $
 *           $Revision: 4079 $
 */
package se.sics.tasim.tacscm.is;

import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import se.sics.isl.util.FormatUtils;
import se.sics.tasim.is.common.Competition;
import se.sics.tasim.is.common.CompetitionParticipant;
import se.sics.tasim.is.common.ScoreGenerator;

public class DefaultScoreGenerator extends ScoreGenerator
{
  
  private static final Logger log = Logger.getLogger(DefaultScoreGenerator.class.getName());
  
  private int agentsToAdvance = 0;
  private String advanceColor = null;
  private boolean isShowingAllAgents = false;
  private boolean isShowingZeroGameAgents = true;
  private boolean isAddingLastUpdated = true;
  private boolean isAddingStatisticsLink = true;
  private boolean isIgnoringWeight = false;
  
  public DefaultScoreGenerator()
  {}
  
  // -------------------------------------------------------------------
  // Settings
  // -------------------------------------------------------------------
  
  public int getAgentsToAdvance()
  {
    return agentsToAdvance;
  }
  
  public void setAgentsToAdvance(int agentsToAdvance)
  {
    this.agentsToAdvance = agentsToAdvance;
  }
  
  public String getAdvanceColor()
  {
    return advanceColor;
  }
  
  public void setAdvanceColor(String advanceColor)
  {
    this.advanceColor = advanceColor;
  }
  
  public boolean isShowingAllAgents()
  {
    return isShowingAllAgents;
  }
  
  public void setShowingAllAgents(boolean isShowingAllAgents)
  {
    this.isShowingAllAgents = isShowingAllAgents;
  }
  
  public boolean isShowingZeroGameAgents()
  {
    return isShowingZeroGameAgents;
  }
  
  public void setShowingZeroGameAgents(boolean isShowingZeroGameAgents)
  {
    this.isShowingZeroGameAgents = isShowingZeroGameAgents;
  }
  
  public boolean isAddingLastUpdated()
  {
    return isAddingLastUpdated;
  }
  
  public void setAddingLastUpdated(boolean isAddingLastUpdated)
  {
    this.isAddingLastUpdated = isAddingLastUpdated;
  }
  
  public boolean isAddingStatisticsLink()
  {
    return isAddingStatisticsLink;
  }
  
  public void setAddingStatisticsLink(boolean isAddingStatisticsLink)
  {
    this.isAddingStatisticsLink = isAddingStatisticsLink;
  }
  
  public boolean isIgnoringWeight()
  {
    return isIgnoringWeight;
  }
  
  public void setIgnoringWeight(boolean isIgnoringWeight)
  {
    this.isIgnoringWeight = isIgnoringWeight;
  }
  
  // -------------------------------------------------------------------
  // Score generation
  // -------------------------------------------------------------------
  
  // Always create the score table... - only works with competitions!!!
  public boolean createScoreTable(Competition competition, int gameID)
  {
    String scoreFile = getScoreFileName();
    try
    {
      // Buffer the complete page first because we do not want to
      // overwrite the file in case something goes wrong.
      String serverName = getServerName();
      boolean isWeightUsed = !isIgnoringWeight && competition.isWeightUsed();
      StringBuffer page = new StringBuffer();
      page.append("<html><head><title>Score Page for ").append(competition.getName()).append(
          "</title></head>\r\n<body>\r\n");
      
      CompetitionParticipant[] users = competition.getParticipants();
      
      if (users != null)
      {
        users = (CompetitionParticipant[]) users.clone();
        Arrays.sort(users, isWeightUsed ? CompetitionParticipant.getAvgWeightedComparator()
            : CompetitionParticipant.getAvgComparator());
        
        page.append("<h3>Scores");
        if (competition != null)
        {
          page.append(" for ").append(competition.getName());
          if (competition.hasSimulationID())
          {
            page.append(" (game ").append(competition.getStartSimulationID()).append(" - ").append(
                competition.getEndSimulationID()).append(')');
          }
        }
        if (serverName != null)
        {
          page.append(" at ").append(serverName);
        }
        page.append("</h3>\r\n");
        page.append("<table border=1>\r\n" + "<tr><th>Position</th><th>Agent</th>");
        if (isWeightUsed)
        {
          page.append("<th>Avg Weighted Score</th>");
        }
        page.append("<th>Average Score</th>" + "<th>Games Played</th><th>Zero Games</th></tr>\r\n");
        int pos = 1;
        for (int i = 0, n = users.length; i < n; i++)
        {
          CompetitionParticipant usr = users[i];
          if (isShowingAllAgents
              || (isShowingZeroGameAgents ? (usr.getGamesPlayed() > 0)
                  : (usr.getGamesPlayed() > usr.getZeroGamesPlayed())))
          {
            String userName = createUserName(usr);
            String color = getAgentColor(usr, pos, n);
            String td, tdright;
            if (color != null)
            {
              td = "<td bgcolor='" + color + "'>";
              tdright = "<td bgcolor='" + color + "' align=right>";
            }
            else
            {
              td = "<td>";
              tdright = "<td align=right>";
            }
            
            page.append("<tr>").append(td).append(pos++).append("</td>").append(td)
                .append(userName);
            if (isWeightUsed)
            {
              page.append("</td>").append(tdright).append(
                  FormatUtils.formatAmount((long) usr.getAvgWeightedScore()));
            }
            page.append("</td>").append(tdright).append(
                FormatUtils.formatAmount((long) usr.getAvgScore()));
            page.append("</td>").append(tdright).append(usr.getGamesPlayed()).append("</td>")
                .append(tdright).append(usr.getZeroGamesPlayed()).append("</td></tr>\r\n");
          }
        }
        page.append("</table>\r\n" + "<p>" + "<b>Zero Games"
            + "</b> is the number of games that resulted in a score "
            + "of zero (probably due to inactivity).<br>");
        addPostInfo(page);
      }
      else
      {
        page.append("No TAC agents registered\r\n");
      }
      if (isAddingLastUpdated)
      {
        addLastUpdated(page);
      }
      page.append("</body>\r\n" + "</html>\r\n");
      
      FileWriter out = new FileWriter(scoreFile);
      out.write(page.toString());
      out.close();
      return true;
      
    }
    catch (Exception e)
    {
      log.log(Level.SEVERE, "could not create score page for game " + gameID + " in " + scoreFile,
          e);
      return false;
    }
  }
  
  // -------------------------------------------------------------------
  // Utility methods - might be overriden by subclasses to give other
  // features
  // -------------------------------------------------------------------
  
  protected String getAgentColor(CompetitionParticipant agent, int pos, int numberOfAgents)
  {
    if (pos <= agentsToAdvance)
    {
      return advanceColor;
    }
    return null;
  }
  
  protected String createUserName(CompetitionParticipant usr)
  {
    // Assume that statistics page exists!
    if (isAddingStatisticsLink)
    {
      return "<a href='" + usr.getID() + ".html'>" + usr.getName() + "</a>";
    }
    return usr.getName();
  }
  
  protected void addLastUpdated(StringBuffer page)
  {
    page.append("<p><hr>\r\n" + "<em>Table last updated ");
    SimpleDateFormat dFormat = new SimpleDateFormat("dd MMM HH:mm:ss");
    Date date = new Date(System.currentTimeMillis());
    dFormat.setTimeZone(new java.util.SimpleTimeZone(0, "UTC"));
    page.append(dFormat.format(date));
    page.append("</em>\r\n");
  }
  
  protected void addPostInfo(StringBuffer page)
  {}
  
} // DefaultScoreGenerator
