/*******************************************************************************
 * Copyright 2015 Alexander Casall, Manuel Mauky
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package dev.webfx.extras.validation.mvvmfx;

import dev.webfx.kit.util.properties.FXProperties;
import dev.webfx.kit.util.properties.Unregisterable;
import javafx.beans.value.ObservableValue;

import java.util.ArrayList;
import java.util.List;


/**
 * This {@link Validator} implementation uses observable values as rules. Compared to the {@link FunctionBasedValidator}
 * this allows more complex validation logic.
 * <p>
 * There are two variants of rules possible:
 * <ul>
 *  <li>"boolean rules" of type ObservableValue&lt;Boolean&gt;</li>
 *  <li>"complex rules" of type ObservableValue&lt;ValidationMessage&gt;</li>
 * </ul>
 *
 * <h2>Boolean Rules of type ObservableValue&lt;Boolean&gt;</h2>
 * <p>
 * The first variant uses an {@link ObservableValue<Boolean>} together with a static message. If this observable
 * has a value of <code>false</code> the validation status will be "invalid" and the given message will be present in the {@link ValidationStatus}
 * of this validator.
 * If the observable has a value of <code>true</code> it is considered to be valid.
 *
 *
 * <h2>Complex Rules of type ObservableValue&lt;ValidationMessage&gt;</h2>
 * <p>
 * The second variant allows more complex rules. It uses a {@link ObservableValue<ValidationMessage>} as rule.
 * If this observable has a value other then <code>null</code> it is considered to be invalid. The {@link ValidationMessage}
 * value will be present in the {@link ValidationStatus} of this validator.
 *
 * <p>
 * <p>
 * You can add multiple rules via the {@link #addRule(ObservableValue, ValidationMessage)} and {@link #addRule(ObservableValue)} method.
 * If multiple rules are violated, each message will be present.
 */
public class ObservableRuleBasedValidator implements Validator {

    private final List<Unregisterable> unregisterableRules = new ArrayList<>();

    private final ValidationStatus validationStatus = new ValidationStatus();

    /**
     * Creates an instance of the Validator without any rules predefined.
     */
    public ObservableRuleBasedValidator() {
    }

    /**
     * Creates an instance of the Validator with the given rule predefined.
     * It's a shortcut for creating an empty validator and
     * adding a single boolean rule with {@link #addRule(ObservableValue, ValidationMessage)}.
     *
     * @param rule
     * @param message
     */
    public ObservableRuleBasedValidator(ObservableValue<Boolean> rule, ValidationMessage message) {
        addRule(rule, message);
    }

    /**
     * Creates an instance of the Validator with the given complex rules predefined.
     * It's a shortcut for creating an empty validator and
     * adding one or multiple complex rules with {@link #addRule(ObservableValue)}.
     *
     * @param rules
     */
    public ObservableRuleBasedValidator(ObservableValue<ValidationMessage>... rules) {
        for (ObservableValue<ValidationMessage> rule : rules) {
            addRule(rule);
        }
    }

    /**
     * Add a rule for this validator.
     * <p>
     * The rule defines a condition that has to be fulfilled.
     * <p>
     * A rule is defined by an observable boolean value. If the rule has a value of <code>true</code> the rule is
     * "fulfilled". If the rule has a value of <code>false</code> the rule is violated. In this case the given message
     * object will be added to the status of this validator.
     * <p>
     * There are some predefined rules for common use cases in the {@link ObservableRules} class that can be used.
     *
     * @param validProperty
     * @param message
     */
    public void addRule(ObservableValue<Boolean> validProperty, ValidationMessage message) {
        unregisterableRules.add(
            FXProperties.runNowAndOnPropertyChange((observable, oldValue, newValue) ->
                    validateBooleanRule(newValue, message)
                , validProperty)
        );
    }

    public void validateBooleanRule(boolean isValid, ValidationMessage message) {
        if (isValid) {
            hideMessage(message);
        } else {
            showMessage(message);
        }
    }

    @Override
    public ValidationStatus getValidationStatus() {
        return validationStatus;
    }

    /**
     * Add a complex rule for this validator.
     * <p>
     * The rule is defined by an {@link ObservableValue}. If this observable contains a {@link ValidationMessage}
     * object the rule is considered to be violated and the {@link ValidationStatus} of this validator will contain
     * the validation message object contained in the observable value.
     * If the observable doesn't contain a value (in other words it contains <code>null</code>) the rule is considered to
     * be fulfilled and the validation status of this validator will be valid (given that no other rule is violated).
     * <p>
     * <p>
     * This method allows some more complex rules compared to {@link #addRule(ObservableValue, ValidationMessage)}.
     * This way you can define rules that have different messages for specific cases.
     *
     * @param rule
     */
    public void addRule(ObservableValue<ValidationMessage> rule) {
        unregisterableRules.add(
            FXProperties.runNowAndOnPropertyChange((observable, oldValue, newValue) -> {
                hideMessage(oldValue); // does nothing if oldValue is null
                showMessage(newValue); // does nothing if newValue is null
            }, rule)
        );
    }

    private void showMessage(ValidationMessage message) {
        if (message != null) {
            validationStatus.addMessage(message);
        }
    }

    private void hideMessage(ValidationMessage message) {
        if (message != null) {
            validationStatus.removeMessage(message);
        }
    }

    public void clear() {
        unregisterableRules.forEach(Unregisterable::unregister);
        unregisterableRules.clear();
        validationStatus.clearMessages();
    }

}
