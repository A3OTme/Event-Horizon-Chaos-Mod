package com.a3ot.eventhorizon.mixin.client;

import com.a3ot.eventhorizon.events.client.CuteFontEvent;
import com.a3ot.eventhorizon.events.utils.CuteOrderedText;
import com.a3ot.eventhorizon.handlers.client.ClientVariables;
import net.minecraft.client.StringSplitter;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.FormattedCharSink;
import net.minecraft.util.StringDecomposer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;

@Mixin(StringSplitter.class)
public class StringSplitterMixin {
    @Redirect(
            method = "stringWidth(Ljava/lang/String;)F",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/StringDecomposer;iterateFormatted(Ljava/lang/String;Lnet/minecraft/network/chat/Style;Lnet/minecraft/util/FormattedCharSink;)Z"
            )
    )
    private boolean modifyStringWidth(String text, Style style, FormattedCharSink sink) {
        String modifiedText = CuteFontEvent.cuteString(text);
        return StringDecomposer.iterateFormatted(modifiedText, style, sink);
    }

    @Redirect(
            method = "stringWidth(Lnet/minecraft/util/FormattedCharSequence;)F",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/FormattedCharSequence;accept(Lnet/minecraft/util/FormattedCharSink;)Z"
            )
    )
    private boolean modifyFormattedCharSequenceWidth(FormattedCharSequence text, FormattedCharSink sink) {
        return ClientVariables.cuteFont ? new CuteOrderedText(text).accept(sink) : text.accept(sink);
    }

    @Redirect(
            method = "stringWidth(Lnet/minecraft/network/chat/FormattedText;)F",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/StringDecomposer;iterateFormatted(Lnet/minecraft/network/chat/FormattedText;Lnet/minecraft/network/chat/Style;Lnet/minecraft/util/FormattedCharSink;)Z"
            )
    )
    private boolean modifyFormattedTextWidth(FormattedText text, Style style, FormattedCharSink sink) {
        StringBuilder rawText = new StringBuilder();
        text.visit(str -> {
            rawText.append(str);
            return Optional.empty();
        });
        String modifiedText = CuteFontEvent.cuteString(rawText.toString());
        return StringDecomposer.iterateFormatted(modifiedText, style, sink);
    }
}
