package com.vaadin.flow.component.dnd;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.server.Command;
import com.vaadin.flow.shared.Registration;

import elemental.json.JsonObject;

public interface DropTarget<T extends Component> extends HasElement {

    String DRAG_OVER_CLASS_NAME = "v-drop-target";
    String DROP_EFFECT_ELEMENT_PROPERTY = "_dropEffect";
    String EVENT_DATA_TRANSFER_DROP_EFFECT = "event.dataTransfer.dropEffect";
    //@formatter:off
    String DRAG_OVER_FUNCTION = "var effect = event.currentTarget['"
            + DROP_EFFECT_ELEMENT_PROPERTY + "'];"
            + "if (effect) {"
            + "event.dataTransfer.dropEffect = effect;}"
            + "event.preventDefault();";
    String DRAG_ENTER_FUNCTION = DRAG_OVER_FUNCTION
            + "event.currentTarget.classList.add('"+DRAG_OVER_CLASS_NAME+"');";
    String DRAG_LEAVE_FUNCTION = // TODO fix to take child elements into account
            "event.currentTarget.classList.remove('"+DRAG_OVER_CLASS_NAME+"');";
    //@formatter:on

    static <T extends Component> DropTarget<T> of(T component) {
        DropTarget<T> dropTarget = new DropTarget<T>() {
            @Override
            public T getDropTargetComponent() {
                return component;
            }
        };
        dropTarget.setActive(true);
        return dropTarget;
    }

    T getDropTargetComponent();

    @Override
    default Element getElement() {
        return getDropTargetComponent().getElement();
    }

    default void setActive(boolean active) {
        if (isActive() != active) {
            if (active) {
                initListeners(this);
            } else {

            }
        }
    }

    default boolean isActive() {
        return getElement().hasProperty("ondrop");
    }

    /**
     * Sets the drop effect for the current drop target. This is set to the
     * dropEffect on {@code dragenter} and {@code dragover} events.
     * <p>
     * <em>NOTE: If the drop effect that doesn't match the dropEffect /
     * effectAllowed of the drag source, it DOES NOT prevent drop on IE and
     * Safari! For FireFox and Chrome the drop is prevented if there they don't
     * match.</em>
     * <p>
     * Default value is browser dependent and can depend on e.g. modifier keys.
     * <p>
     * From Mozilla Foundation: "You can modify the dropEffect property during
     * the dragenter or dragover events, if for example, a particular drop
     * target only supports certain operations. You can modify the dropEffect
     * property to override the user effect, and enforce a specific drop
     * operation to occur. Note that this effect must be one listed within the
     * effectAllowed property. Otherwise, it will be set to an alternate value
     * that is allowed."
     *
     * @param dropEffect
     *            the drop effect to be set or {@code null} to not modify
     */
    default void setDropEffect(DropEffect dropEffect) {
        if (!Objects.equals(getDropEffect(), dropEffect)) {
            if (dropEffect == null) {
                getElement().removeProperty(DROP_EFFECT_ELEMENT_PROPERTY);
            } else {
                getElement().setProperty(DROP_EFFECT_ELEMENT_PROPERTY,
                        dropEffect.toString().toLowerCase());
            }
        }
    }

    /**
     * Returns the drop effect for the current drop target.
     *
     * @return The drop effect of this drop target or {@code null} if none set
     * @see #setDropEffect(DropEffect)
     */
    default DropEffect getDropEffect() {
        String dropEffect = getElement()
                .getProperty(DROP_EFFECT_ELEMENT_PROPERTY, null);
        return dropEffect == null ? null : DropEffect.valueOf(dropEffect.toUpperCase());
    }

    /**
     * Attaches drop listener for the current drop target.
     * {@link DropListener#drop(DropEvent)} is called when drop event happens on
     * the client side.
     *
     * @param listener
     *            Listener to handle drop event.
     * @return Handle to be used to remove this listener.
     */
    default Registration addDropListener(DropListener<T> listener) {
        return ComponentUtil.addListener(getDropTargetComponent(),
                DropEvent.class, (DropListener) listener);
    }

    static <T extends Component> void initListeners(DropTarget<T> dropTarget) {
        Element element = dropTarget.getElement();
        element.setAttribute("ondragover", DRAG_OVER_FUNCTION);
        element.setAttribute("ondragenter", DRAG_ENTER_FUNCTION);
        element.setAttribute("ondragleave",DRAG_LEAVE_FUNCTION);
        element.addEventListener("drop", event -> {
            JsonObject eventData = event.getEventData();
            Map<String, String> dataPreserveOrder = new LinkedHashMap<>();
            DropEffect dropEffect = dropTarget.getDropEffect();
            DragSource<? extends Component> dragSource = UI.getCurrent()
                    .getActiveDragSource();
            if (eventData.hasKey(EVENT_DATA_TRANSFER_DROP_EFFECT)) {
                dropEffect = DropEffect.valueOf(eventData
                        .getString(EVENT_DATA_TRANSFER_DROP_EFFECT).toUpperCase());
            }
            Component dropTargetComponent = dropTarget.getDropTargetComponent();
            ComponentUtil.fireEvent(dropTargetComponent,
                    new DropEvent<T>(dropTarget.getDropTargetComponent(), dataPreserveOrder,
                            dropEffect, dragSource));
        }).addEventData(EVENT_DATA_TRANSFER_DROP_EFFECT);
    }
}
