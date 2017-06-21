package fr.tnducrocq.boxe.model;

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

public class Boxer {

    @Text(".boxerTitle")
    public String title;

    @Text(".bgwonBlock > span")
    public String won;

    @Text(".bglostBlock > span")
    public String lost;

    @Text(".bgdrawBlock > span")
    public String draw;

    public List<Result> last6;

    @TextOptional(".profileDetails table tr:eq(2) td:eq(1) div:eq(1) li")
    public String ranking;

    public void parse(Element body) {
        Elements elts = body.select(".profilePhoto").first().select(".last_6");
        if (elts != null) {
            last6 = new ArrayList<>();
            for (Element elt : elts) {
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
        Element profileDetails = body.select(".profileDetails").first();
        Element profileDetailsTable = profileDetails.select("table").first();
        try {
            ranking = profileDetailsTable.select("tr").get(2).select("td").get(1).select("div").get(1).select("a").first().text();
        } catch (Exception e) {
        }
        System.out.println(ranking);
    }


    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Boxer{");
        sb.append("title='").append(title).append('\'');
        sb.append(", won='").append(won).append('\'');
        sb.append(", lost='").append(lost).append('\'');
        sb.append(", draw='").append(draw).append('\'');
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
