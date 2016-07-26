package org.catrobat.estimationplugin.helper;

public class HtmlHelper {

    public static String getHtmlLink(String href, String display, String title) {
        return "<a title=\"" + title + "\" href=\"" + href + "\">" + display + "</a>";
    }
}
