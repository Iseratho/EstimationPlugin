package ut.org.catrobat.estimationplugin;

import org.catrobat.estimationplugin.helper.HtmlHelper;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HtmlHelperTest {

    @Test
    public void testGetHtmlLink() {
        String href = "testlink";
        String display = "testdiplay";
        String title = "testttitle";
        String output = HtmlHelper.getHtmlLink(href, display, title);
        assertEquals("<a title=\"testttitle\" href=\"testlink\">testdiplay</a>", output);
    }

}
