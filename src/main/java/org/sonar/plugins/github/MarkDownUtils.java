/*
 * SonarQube :: GitHub Plugin
 * Copyright (C) 2015 SonarSource
 * sonarqube@googlegroups.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.plugins.github;

import org.sonar.api.BatchComponent;
import org.sonar.api.CoreProperties;
import org.sonar.api.batch.InstantiationStrategy;
import org.sonar.api.config.Settings;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import javax.annotation.Nullable;

@InstantiationStrategy(InstantiationStrategy.PER_BATCH)
public class MarkDownUtils implements BatchComponent {

  private static final String IMAGES_ROOT_URL = "https://raw.githubusercontent.com/Miovision/sonar-github/master/images/";
  private final String ruleUrlPrefix;

  public MarkDownUtils(Settings settings) {
    // If server base URL was not configured in SQ server then is is better to take URL configured on batch side
    String baseUrl = settings.hasKey(CoreProperties.SERVER_BASE_URL) ? settings.getString(CoreProperties.SERVER_BASE_URL) : settings.getString("sonar.host.url");
    if (!baseUrl.endsWith("/")) {
      baseUrl += "/";
    }
    this.ruleUrlPrefix = baseUrl;
  }

  public String inlineIssue(String severity, String message, String ruleKey, boolean isNew, String issueKey) {
    String ruleLink = getRuleLink(ruleKey);
    StringBuilder sb = new StringBuilder();
    sb.append(getImageMarkdownForSeverity(severity))
      .append(" ")
      .append(message)
      .append(" ")
      .append(ruleLink)
      .append(" ")
      .append(getIssueLink(isNew, issueKey));
    return sb.toString();
  }

  public String globalIssue(String severity, String message, String ruleKey, @Nullable String url, String componentKey, boolean isNew, String issueKey) {
    String ruleLink = getRuleLink(ruleKey);
    StringBuilder sb = new StringBuilder();
    sb.append(getImageMarkdownForSeverity(severity)).append(" ");
    if (url != null) {
      sb.append("[").append(message).append("]").append("(").append(url).append(")");
    } else {
      sb.append(message).append(" ").append("(").append(componentKey).append(")");
    }
    sb.append(" ").append(ruleLink).append(" ").append(getIssueLink(isNew, issueKey));
    return sb.toString();
  }

  String getRuleLink(String ruleKey) {
    return "[![rule](" + IMAGES_ROOT_URL + "rule.png)](" + ruleUrlPrefix + "coding_rules#rule_key=" + encodeForUrl(ruleKey) + ")";
  }

  String getIssueLink(boolean isNew, String issueKey) {
    if (isNew) {
      return "![NEW](" + IMAGES_ROOT_URL + "newissue.png)";
    }
    return "[![PERMALINK](" + IMAGES_ROOT_URL + "permalink.png)](" + ruleUrlPrefix + "issues/search#issues=" + issueKey + ")";
  }

  static String encodeForUrl(String url) {
    try {
      return URLEncoder.encode(url, "UTF-8");

    } catch (UnsupportedEncodingException e) {
      throw new IllegalStateException("Encoding not supported", e);
    }
  }

  public static String getImageMarkdownForSeverity(String severity) {
    return "![" + severity + "](" + IMAGES_ROOT_URL + "severity-" + severity.toLowerCase() + ".png)";
  }

}
