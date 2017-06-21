package fr.tnducrocq.boxe.model;

import com.fcannizzaro.jsoup.annotations.exceptions.AttributeNotFoundException;
import com.fcannizzaro.jsoup.annotations.exceptions.ElementNotFoundException;
import com.fcannizzaro.jsoup.annotations.interfaces.AfterBind;
import com.fcannizzaro.jsoup.annotations.interfaces.Attr;
import com.fcannizzaro.jsoup.annotations.interfaces.Child;
import com.fcannizzaro.jsoup.annotations.interfaces.ForEach;
import com.fcannizzaro.jsoup.annotations.interfaces.Html;
import com.fcannizzaro.jsoup.annotations.interfaces.Items;
import com.fcannizzaro.jsoup.annotations.interfaces.Selector;
import com.fcannizzaro.jsoup.annotations.interfaces.Text;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by tony on 21/06/2017.
 */

public class CustomJsoupProcessor {
    public CustomJsoupProcessor() {
    }

    private static Element element(Element container, String query, boolean optional) {
        Elements select = container.select(query);
        if (select.size() == 0) {
            if (!optional) throw new ElementNotFoundException(query);
            return null;
        } else {
            return select.first();
        }
    }

    private static Object valueOf(Element container, AnnotatedElement field) {
        Selector selector = (Selector) field.getAnnotation(Selector.class);
        Text text = (Text) field.getAnnotation(Text.class);
        TextOptional textOptional = (TextOptional) field.getAnnotation(TextOptional.class);
        Child child = (Child) field.getAnnotation(Child.class);
        Items items = (Items) field.getAnnotation(Items.class);
        Html html = (Html) field.getAnnotation(Html.class);
        Attr attr = (Attr) field.getAnnotation(Attr.class);
        String value = null;
        if (field instanceof Field) {
            Field f = (Field) field;
            if (items != null) {
                ParameterizedType cz1 = (ParameterizedType) f.getGenericType();
                Class sel1 = (Class) cz1.getActualTypeArguments()[0];
                return fromList(container, sel1);
            }

            if (child != null) {
                Class cz = f.getType();
                Selector sel = (Selector) cz.getAnnotation(Selector.class);
                if (sel != null) {
                    return from(element(container, sel.value(), false), cz);
                }
            }
        }

        if (selector != null) {
            return element(container, selector.value(), false);
        } else {
            Element el;
            if (text != null) {
                el = element(container, text.value(), false);
                if (el != null) {
                    return el.text();
                }
            } else if (textOptional != null) {
                el = element(container, textOptional.value(), true);
                if (el != null) {
                    return el.text();
                }
            } else if (html != null) {
                el = element(container, html.value(), false);
                if (el != null) {
                    return el.html();
                }
            } else if (attr != null) {
                el = element(container, attr.query(), false);
                if (el != null) {
                    value = el.attr(attr.attr());
                    if (value == null) {
                        throw new AttributeNotFoundException(attr.attr());
                    }
                }
            }

            return value;
        }
    }

    public static <T> T from(Element container, Class<T> clazz) {
        try {
            Object e = clazz.newInstance();
            Field[] afterBindMethod = clazz.getDeclaredFields();
            int var4 = afterBindMethod.length;

            int var5;
            for (var5 = 0; var5 < var4; ++var5) {
                Field field = afterBindMethod[var5];
                Object method = valueOf(container, field);
                if (method != null) {
                    field.setAccessible(true);
                    field.set(e, method);
                }
            }

            Method var15 = null;
            Method[] var16 = clazz.getDeclaredMethods();
            var5 = var16.length;

            for (int var17 = 0; var17 < var5; ++var17) {
                Method var18 = var16[var17];
                ForEach forEach = (ForEach) var18.getAnnotation(ForEach.class);
                AfterBind afterBind = (AfterBind) var18.getAnnotation(AfterBind.class);
                var18.setAccessible(true);
                Object value = valueOf(container, var18);
                if (value != null) {
                    var18.invoke(e, new Object[]{value});
                } else if (afterBind != null) {
                    var15 = var18;
                } else if (forEach != null) {
                    Elements elements = container.select(forEach.value());

                    for (int i = 0; i < elements.size(); ++i) {
                        Element element = (Element) elements.get(i);
                        if (var18.getParameterTypes().length > 1) {
                            var18.invoke(e, new Object[]{element, Integer.valueOf(i)});
                        } else {
                            var18.invoke(e, new Object[]{element});
                        }
                    }
                }
            }

            if (var15 != null) {
                var15.invoke(e, new Object[0]);
            }

            return (T) e;
        } catch (Exception var14) {
            var14.printStackTrace();
            return null;
        }
    }

    public static <T> List<T> fromList(Element container, Class<T> clazz) {
        ArrayList items = new ArrayList();
        Selector selector = (Selector) clazz.getAnnotation(Selector.class);
        if (selector != null) {
            Elements elements = container.select(selector.value());
            Iterator var5 = elements.iterator();

            while (var5.hasNext()) {
                Element element = (Element) var5.next();
                items.add(from(element, clazz));
            }
        }

        return items;
    }
}
