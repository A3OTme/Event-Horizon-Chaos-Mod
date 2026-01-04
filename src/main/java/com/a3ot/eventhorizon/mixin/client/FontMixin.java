package com.a3ot.eventhorizon.mixin.client;

import com.a3ot.eventhorizon.events.client.CuteFontEvent;
import com.a3ot.eventhorizon.events.utils.CuteOrderedText;
import com.a3ot.eventhorizon.handlers.client.ClientVariables;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.FormattedCharSink;
import net.minecraft.util.StringDecomposer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(Font.class)
public class FontMixin {
    @Redirect(
            method = "renderText(Ljava/lang/String;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;II)F",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/StringDecomposer;iterateFormatted(Ljava/lang/String;Lnet/minecraft/network/chat/Style;Lnet/minecraft/util/FormattedCharSink;)Z"
            )
    )
    private boolean modifyStringText(String text, Style style, FormattedCharSink sink) {
        String modifiedText = CuteFontEvent.cuteString(text);
        return StringDecomposer.iterateFormatted(modifiedText, style, sink);
    }

    @Redirect(
            method = "renderText(Lnet/minecraft/util/FormattedCharSequence;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;II)F",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/FormattedCharSequence;accept(Lnet/minecraft/util/FormattedCharSink;)Z"
            )
    )
    private boolean modifyFormattedText(FormattedCharSequence text, FormattedCharSink sink) {
        return ClientVariables.cuteFont ? new CuteOrderedText(text).accept(sink) : text.accept(sink);
    }
}
