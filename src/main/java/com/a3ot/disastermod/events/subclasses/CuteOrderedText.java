package com.a3ot.disastermod.events.subclasses;

import com.a3ot.disastermod.events.client.CuteFontEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.FormattedCharSink;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Original source: <a href="https://github.com/MayaqqDev/Cynosure/blob/master/common/src/main/kotlin/dev/mayaqq/cynosure/utils/fun/UwUOrderedText.kt"> MayaqqDev/Cynosure (UwUOrderedText.kt)</a>
 **/
public class CuteOrderedText implements FormattedCharSequence {
    private final FormattedCharSequence composite;

    public CuteOrderedText(FormattedCharSequence wrapped) {
        List<CuteFragment> fragments = extractFragments(wrapped);
        List<String> transformedFragments = new ArrayList<>();
        List<Style> styles = new ArrayList<>();

        // Обработка фрагментов и сборка в общий текст
        StringBuilder combinedText = new StringBuilder();
        for (CuteFragment fragment : fragments) {
            String transformed = CuteFontEvent.localTransform(fragment.getText());
            transformedFragments.add(transformed);
            combinedText.append(transformed);
            styles.add(fragment.getStyle());
        }

        // Глобальное преобразование
        String globalText = CuteFontEvent.globalTransform(combinedText.toString());

        // Разделение на фрагменты с сохранением стиля
        List<FormattedCharSequence> globalFragments = new ArrayList<>();
        int pos = 0;
        for (int i = 0; i < fragments.size() && pos < globalText.length(); i++) {
            int length = transformedFragments.get(i).length();
            int end = Math.min(pos + length, globalText.length());
            globalFragments.add(FormattedCharSequence.forward(globalText.substring(pos, end), styles.get(i)));
            pos = end;
        }

        // Добавление остатка
        if (pos < globalText.length()) {
            globalFragments.add(FormattedCharSequence.forward(globalText.substring(pos),
                    !styles.isEmpty() ? styles.getLast() : Style.EMPTY));
        }

        this.composite = FormattedCharSequence.composite(globalFragments);
    }

    // Existing extractFragments method remains unchanged
    private List<CuteFragment> extractFragments(FormattedCharSequence sequence) {
        List<CuteFragment> fragments = new ArrayList<>();
        final StringBuilder[] currentText = {new StringBuilder()};
        final Style[] currentStyle = {null};

        sequence.accept((index, style, codePoint) -> {
            if (currentStyle[0] == null) {
                currentStyle[0] = style;
                currentText[0].appendCodePoint(codePoint);
                return true;
            }
            if (!style.equals(currentStyle[0]) && !currentText[0].isEmpty()) {
                fragments.add(new CuteFragment(currentStyle[0], currentText[0].toString()));
                currentText[0] = new StringBuilder();
                currentStyle[0] = style;
            }
            currentText[0].appendCodePoint(codePoint);
            return true;
        });

        if (!currentText[0].isEmpty()) {
            fragments.add(new CuteFragment(currentStyle[0], currentText[0].toString()));
        }

        return fragments;
    }

    @Override
    public boolean accept(@NotNull FormattedCharSink visitor) {
        return composite.accept(visitor);
    }
}
