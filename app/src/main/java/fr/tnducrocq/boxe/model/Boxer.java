package fr.tnducrocq.boxe.model;

import com.fcannizzaro.jsoup.annotations.interfaces.IParsable;
import com.fcannizzaro.jsoup.annotations.interfaces.Text;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tony on 19/06/2017.
 */

enum Result {

    Win, Loss, Draw, NoContest, Pending;

}

public class Boxer implements IParsable {

    @Text(".boxerTitle")
    public String title;

    @Text(".bgwonBlock > span")
    public String won;

    @Text(".bglostBlock > span")
    public String lost;

    @Text(".bgdrawBlock > span")
    public String draw;

    public List<Result> last6;

    @Text(value = ".profileDetails table tr:eq(2) td:eq(1) div:eq(1) a", optional = true)
    public String ranking;

    public void parse(Element body) {
        Element profilePhoto = body.select(".profilePhoto").first();
        if (profilePhoto != null) {
            Elements last6Elts = profilePhoto.select(".last_6");
            if (last6Elts != null) {
                last6 = new ArrayList<>();
                for (Element elt : last6Elts) {
                    String attr = elt.attr("class");
                    if (attr.contains("last_6_w")) {
                        last6.add(Result.Win);
                    } else if (attr.contains("last_6_l")) {
                        last6.add(Result.Loss);
                    } else if (attr.contains("last_6_d")) {
                        last6.add(Result.Draw);
                    }
                }
            }
        }
    }


    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Boxer{");
        sb.append("title='").append(title).append('\'');
        sb.append(", won='").append(won).append('\'');
        sb.append(", lost='").append(lost).append('\'');
        sb.append(", draw='").append(draw).append('\'');
        sb.append(", ranking='").append(ranking).append('\'');
        if (last6 != null) {
            sb.append(", last6='");
            for (Result r : last6) {
                sb.append(r + " ");
            }
            sb.append('\'');
        }
        sb.append('}');
        return sb.toString();
    }
}
