package Replicator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

class Cleaner {

    private String[] tagsToRemove = ("script style link form header footer noscript").split(" ");
    private String[] elementsToRemove = ("social mailchimp calendar sidebar-post mistape comment header footer menu banner auth subscribe nav follow share meta").split(" ");

    private Document document;

    Cleaner(Document doc) {
        document = doc;
    }

    Document clean() {
        removeTags();
        unwrapTextTags();
        unwrapParagraphs();

        return document;
    }

    private void removeTags() {
        Elements elements;

        for(String regex: elementsToRemove) {
            elements = document.select("*[id*=" + regex + "]");
            elements.remove();
        }

        for(String regex: elementsToRemove) {
            elements = document.select("*[class*=" + regex + "]");
            elements.remove();
        }

        elements = document.select("*");

        for(Element element: elements) {
            if (!element.hasText() && element.isBlock()) {
                if(element.getElementsByTag("img").size() == 0) {
                    element.remove();
                }
            }
        }

        for(String tag: tagsToRemove) {
            document.getElementsByTag(tag).remove();
        }

        document = Jsoup.parse(Jsoup.clean(document.html(), Whitelist.relaxed()));
    }

    private void unwrapTextTags() {
        Elements elements = new Elements();

        elements.addAll(document.getElementsByTag("em"));
        elements.addAll(document.getElementsByTag("span"));

        for(Element element: elements) {
            element.unwrap();
        }
    }

    private void unwrapParagraphs() {
        Elements elements = document.select("div");

        for(Element element: elements) {
            Elements children = element.getElementsByTag("p");

            if(children.size() > 1) {
                for(Element child: children) {
                    child.unwrap();
                }
            }
        }
    }

}