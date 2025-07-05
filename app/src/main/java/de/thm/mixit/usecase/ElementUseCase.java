package de.thm.mixit.usecase;

import android.content.Context;
import android.util.Log;

import java.util.function.Consumer;

import de.thm.mixit.data.entities.CombinationEntity;
import de.thm.mixit.data.entities.ElementEntity;
import de.thm.mixit.data.repository.CombinationRepository;
import de.thm.mixit.data.repository.ElementRepository;

public class ElementUseCase {

        private static final String TAG = ElementUseCase.class.getSimpleName();

        private final CombinationRepository combinationRepository;
        private final ElementRepository elementRepository;

        /**
         * Constructor for ElementUseCase.
         * Initializes the repositories needed for element operations.
         *
         * @param context The Android context used to create the repositories.
         */
        public ElementUseCase(Context context) {
            this.combinationRepository = CombinationRepository.create(context);
            this.elementRepository = ElementRepository.create(context);
        }

        /**
         * Combines two elements to create a new element.
         * If the combination already exists, it retrieves the existing element.
         * Otherwise, it generates a new element and stores the combination.
         *
         * @param element1 The first element to combine.
         * @param element2 The second element to combine.
         * @param callback A callback to receive the resulting ElementEntity.
         */
        public void getElement(String element1, String element2,
                               Consumer<ElementEntity> callback) {
            // Check if there is already a combination for the two elements
            combinationRepository.findByCombination(element1, element2,
                    combination -> {
                // If a combination exists, retrieve the output element
                if (combination != null) {
                    Log.d(TAG, "Combination found for element: "
                            + combination.inputA + " + " + combination.inputB
                            + " with outputId: " + combination.outputId);
                    elementRepository.findById(combination.outputId, callback::accept);
                } else {
                    Log.d(TAG, "No combination found for combination: "
                            + element1 + " + " + element2);
                    // If no combination exists, generate a new element
                    elementRepository.generateNew(element1, element2, newElement -> {
                        Log.d(TAG, "Generated new element: "
                                + newElement.emoji + " " + newElement.output);
                        // Check if the new element already exists in the repository
                        elementRepository.findByName(newElement.output,
                                existingElement -> {
                            // If the element exists, return it via the callback
                            if (existingElement != null) {
                                Log.d(TAG, "Element already exists: "
                                        + existingElement.emoji + " " + existingElement.output);
                                combinationRepository.insertCombination(new CombinationEntity(
                                        element1, element2, existingElement.id),
                                        c -> {
                                    Log.d(TAG, "Combination inserted for existing element: "
                                            + element1 + " + " + element2);
                                    callback.accept(existingElement);
                                });
                            } else {
                                Log.d(TAG, "Element does not exist, inserting new element: "
                                        + newElement.emoji + " " + newElement.output);
                                // If the element does not exist, insert
                                // it and create a new combination
                                elementRepository.insertElement(newElement,
                                        insertedElement -> {
                                    Log.d(TAG, "Inserted new element with ID: "
                                            + insertedElement.id);
                                    combinationRepository.insertCombination(new CombinationEntity(
                                            element1, element2, insertedElement.id),
                                            c -> {
                                        Log.d(TAG, "Combination inserted for: "
                                                + element1 + " + " + element2);
                                        callback.accept(insertedElement);
                                    });
                                });
                            }
                        });
                    });
                }
            });
        }
}
