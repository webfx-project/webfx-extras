package dev.webfx.extras.webtext.peers.base;

import dev.webfx.extras.webtext.HtmlText;
import dev.webfx.kit.mapper.peers.javafxcontrols.base.ControlPeerMixin;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

/**
 * @author Bruno Salmon
 */
public interface HtmlTextPeerMixin
        <N extends HtmlText, NB extends HtmlTextPeerBase<N, NB, NM>, NM extends HtmlTextPeerMixin<N, NB, NM>>

        extends ControlPeerMixin<N, NB, NM> {

    void updateText(String text);

    void updateFont(Font font);

    void updateFill(Paint fill);

}
