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

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.shared.Registration;

/**
 * @author Vaadin Ltd
 *
 */
public class DropTargetComponent<T extends Component> extends Composite<T> implements DropTarget<T> {

    private final T origin;

    private DropEffect dropEffect;

    private String criteriaScript;

    private Criterion.Match criteriaMatch;

    private List<Criterion> criteria;

    public DropTargetComponent(T component) {
        origin = component;
    }

    @Override
    public T getDropTargetComponent() {
        return getContent();
    }

    /**
     * Sets a criteria script in JavaScript to allow drop on this drop target.
     * The script is executed when something is dragged on top of the target,
     * and the drop is not allowed in case the script returns {@code false}.
     * <p>
     * Drop will be allowed if it passes both this criteria script and the
     * criteria set via any of {@code setDropCriterion()} or {@code
     * setDropCriteria()} methods. If no criteria is set, then the drop is
     * always accepted, if the set {@link #setDropEffect(DropEffect) dropEffect}
     * matches the drag source.
     * <p>
     * <b>IMPORTANT:</b> Construct the criteria script carefully and do not
     * include untrusted sources such as user input. Always keep in mind that
     * the script is executed on the client as is.
     * <p>
     * Example:
     *
     * <pre>
     * target.setDropCriterion(
     *         // If dragged source contains a URL, allow it to be dropped
     *         "if (event.dataTransfer.types.includes('text/uri-list')) {"
     *                 + "    return true;" + "}" +
     *
     *                 // Otherwise cancel the event
     *                 "return false;");
     * </pre>
     *
     * @param criteriaScript
     *            JavaScript to be executed when drop event happens or
     *            {@code null} to clear.
     */
    public void setDropCriteriaScript(String criteriaScript) {
        if (!Objects.equals(this.criteriaScript, criteriaScript)) {
            this.criteriaScript = criteriaScript;
        }
    }

    /**
     * Gets the criteria script that determines whether a drop is allowed. If
     * the script returns {@code false}, then it is determined the drop is not
     * allowed.
     *
     * @return JavaScript that executes when drop event happens.
     * @see #setDropCriteriaScript(String)
     */
    public String getDropCriteriaScript() {
        return criteriaScript;
    }

    /**
     * Set a drop criterion to allow drop on this drop target. When data is
     * dragged on top of the drop target, the given value is compared to the
     * drag source's payload with the same key. The drag passes this criterion
     * if the value of the payload and the value given here are equal.
     * <p>
     * Note that calling this method will overwrite the previously set criteria.
     * To set multiple criteria, call the
     * {@link #setDropCriteria(Criterion.Match, Criterion...)} method.
     * <p>
     * To handle more complex criteria, define a custom script with
     * {@link #setDropCriteriaScript(String)}. Drop will be allowed if both this
     * criterion and the criteria script are passed.
     *
     * @param key
     *            key of the payload to be compared
     * @param value
     *            value to be compared to the payload's value
     * @see DragSourceExtension#setPayload(String, String)
     */
    public void setDropCriterion(String key, String value) {
        setDropCriteria(Criterion.Match.ANY, new Criterion(key, value));
    }

    /**
     * Set a drop criterion to allow drop on this drop target. When data is
     * dragged on top of the drop target, the given value is compared to the
     * drag source's payload with the same key. The drag passes this criterion
     * if the value of the payload compared to the given value using the given
     * operator holds.
     * <p>
     * Note that calling this method will overwrite the previously set criteria.
     * To set multiple criteria, call the
     * {@link #setDropCriteria(Criterion.Match, Criterion...)} method.
     * <p>
     * To handle more complex criteria, define a custom script with
     * {@link #setDropCriteriaScript(String)}. Drop will be allowed if both this
     * criterion and the criteria script are passed.
     *
     * @param key
     *            key of the payload to be compared
     * @param operator
     *            comparison operator to be used
     * @param value
     *            value to be compared to the payload's value
     * @see DragSourceExtension#setPayload(String, int)
     */
    public void setDropCriterion(String key, ComparisonOperator operator,
            int value) {
        setDropCriteria(Criterion.Match.ANY,
                new Criterion(key, operator, value));
    }

    /**
     * Set a drop criterion to allow drop on this drop target. When data is
     * dragged on top of the drop target, the given value is compared to the
     * drag source's payload with the same key. The drag passes this criterion
     * if the value of the payload compared to the given value using the given
     * operator holds.
     * <p>
     * Note that calling this method will overwrite the previously set criteria.
     * To set multiple criteria, call the
     * {@link #setDropCriteria(Criterion.Match, Criterion...)} method.
     * <p>
     * To handle more complex criteria, define a custom script with
     * {@link #setDropCriteriaScript(String)}. Drop will be allowed if both this
     * criterion and the criteria script are passed.
     *
     * @param key
     *            key of the payload to be compared
     * @param operator
     *            comparison operator to be used
     * @param value
     *            value to be compared to the payload's value
     * @see DragSourceExtension#setPayload(String, double)
     */
    public void setDropCriterion(String key, ComparisonOperator operator,
            double value) {
        setDropCriteria(Criterion.Match.ANY,
                new Criterion(key, operator, value));
    }

    /**
     * Sets multiple drop criteria to allow drop on this drop target. When data
     * is dragged on top of the drop target, the value of the given criteria is
     * compared to the drag source's payload with the same key.
     * <p>
     * The drag passes these criteria if, depending on {@code match}, any or all
     * of the criteria matches the payload, that is the value of the payload
     * compared to the value of the criterion using the criterion's operator
     * holds.
     * <p>
     * Note that calling this method will overwrite the previously set criteria.
     * <p>
     * To handle more complex criteria, define a custom script with
     * {@link #setDropCriteriaScript(String)}. Drop will be allowed if both this
     * criterion and the criteria script are passed.
     *
     * @param match
     *            defines whether any or all of the given criteria should match
     *            to allow drop on this drop target
     * @param criteria
     *            criteria to be compared to the payload
     */
    public void setDropCriteria(Criterion.Match match, Criterion... criteria) {
        criteriaMatch = match;
        this.criteria = Arrays.asList(criteria);
    }

    @Override
    protected T initContent() {
        return origin;
    }
}
