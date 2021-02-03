package org.vaadin.gwt290.client.samples;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.user.client.DOM;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.client.ui.VTextField;
import com.vaadin.client.ui.textfield.AbstractTextFieldConnector;
import com.vaadin.shared.ui.Connect;
import org.vaadin.gwt290.samples.ResetButtonForTextField;

/**
 * Client side implementation of {@link ResetButtonForTextField}.
 * 
 * @see <a href="https://vaadin.com/blog/-/blogs/2656782">Extending components
 *      in Vaadin 7</a>
 */
@Connect(ResetButtonForTextField.class)
public class ResetButtonForTextFieldConnector extends
        AbstractExtensionConnector implements KeyUpHandler, AttachEvent.Handler {

    public static final String CLASSNAME = "resetbuttonfortextfield";
    private AbstractTextFieldConnector textFieldConnector;
    private VTextField textField;
    private Element resetButtonElement;

    @Override
    protected void extend(ServerConnector serverConnector) {
        serverConnector.addStateChangeHandler(event -> {
            Scheduler.get().scheduleDeferred(() -> {
                updateResetButtonVisibility();
            });
        });
        textFieldConnector = (AbstractTextFieldConnector) serverConnector;
        textField = (VTextField) textFieldConnector.getWidget();
        var textFieldStyle = CLASSNAME + "-textfield";
        textField.addStyleName(textFieldStyle);

        resetButtonElement = DOM.createDiv();
        var resetButtonStyle = CLASSNAME + "-resetbutton";
        resetButtonElement.addClassName(resetButtonStyle);

        textField.addAttachHandler(this);
        textField.addKeyUpHandler(this);
    }

    private void updateResetButtonVisibility() {
        if (textField.getValue().isEmpty()
                || textField.getStyleName().contains("v-textfield-prompt")) {
            resetButtonElement.getStyle().setDisplay(Style.Display.NONE);
        } else {
            resetButtonElement.getStyle().clearDisplay();
        }
    }

    public native void addResetButtonClickListener(Element el)
    /*-{
        var self = this;
        el.onclick = $entry(function () {
            self.@org.vaadin.gwt290.client.samples.ResetButtonForTextFieldConnector::clearTextField()();
        });
    }-*/;

    public native void removeResetButtonClickListener(Element el)
    /*-{
        el.onclick = null;
    }-*/;

    @Override
    public void onKeyUp(KeyUpEvent keyUpEvent) {
        updateResetButtonVisibility();
    }

    @Override
    public void onAttachOrDetach(AttachEvent attachEvent) {
        if (attachEvent.isAttached()) {
            textField.getElement().getParentElement()
                    .insertAfter(resetButtonElement, textField.getElement());
            updateResetButtonVisibility();
            addResetButtonClickListener(resetButtonElement);
        } else {
            var parentElement = resetButtonElement.getParentElement();
            if (parentElement != null) {
                parentElement.removeChild(resetButtonElement);
            }
            removeResetButtonClickListener(resetButtonElement);
        }
    }

    private void clearTextField() {
        textField.setValue("");
        textFieldConnector.flush();
        updateResetButtonVisibility();
        textField.getElement().focus();
    }
}
