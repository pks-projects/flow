/*
 * Copyright 2000-2018 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.flow.component.dnd;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;

/**
 * HTML5 drag end event.
 *
 * @param <T>
 *            Type of the component that was dragged.
 * @author Vaadin Ltd
 * @see DragSourceComponent#addDragEndListener(DragEndListener)
 */
public class DragEndEvent<T extends Component> extends ComponentEvent<T> {
    private final DropEffect dropEffect;
    private final DragSource<T> dragSource;

    /**
     * Creates a drag end event.
     *
     * @param dragSource
     *            the drag source
     * @param source
     *            Component that was dragged.
     * @param dropEffect
     *            Drop effect from {@code DataTransfer.dropEffect} object.
     */
    public DragEndEvent(DragSource<T> dragSource, T source,
            DropEffect dropEffect) {
        super(source, true);
        this.dragSource = dragSource;
        this.dropEffect = dropEffect;
    }

    /**
     * Get drop effect of the dragend event. The value will be the desired
     * action, that is the dropEffect value of the last dragenter or dragover
     * event. The value depends on the effectAllowed parameter of the drag
     * source, the dropEffect parameter of the drop target, and its drag over
     * and drop criteria.
     * <p>
     * If the drop is not successful, the value will be {@code NONE}.
     * <p>
     * In case the desired drop effect is {@code MOVE}, the data being dragged
     * should be removed from the source.
     *
     * @return The {@code DataTransfer.dropEffect} parameter of the client side
     *         dragend event.
     * @see DragSourceComponent#setEffectAllowed(com.vaadin.flow.component.dnd.EffectAllowed)
     * @see DropTargetComponent#setDropEffect(DropEffect)
     * @see DropTargetComponent#setDropCriteriaScript(String)
     */
    public DropEffect getDropEffect() {
        return dropEffect;
    }

    /**
     * Returns whether the drag event was cancelled. This is a shorthand for
     * {@code dropEffect == NONE}.
     *
     * @return {@code true} if the drop event was cancelled, {@code false}
     *         otherwise.
     */
    public boolean isCanceled() {
        return getDropEffect() == DropEffect.NONE;
    }

    /**
     * Returns the drag source component where the dragend event occurred.
     *
     * @return Component which was dragged.
     */
    public T getComponent() {
        return getSource();
    }

    /**
     * Returns the drag source where the dragend event occurred.
     *
     * @return the drag source
     */
    public DragSource<T> getDragSource() {
        return dragSource;
    }
}
