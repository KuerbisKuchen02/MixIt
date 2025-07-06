package de.thm.mixit.domain.usecase;

import android.content.Context;
import android.util.Log;

import java.util.function.Consumer;

import de.thm.mixit.data.entities.CombinationEntity;
import de.thm.mixit.data.entities.ElementEntity;
import de.thm.mixit.data.repository.CombinationRepository;
import de.thm.mixit.data.repository.ElementRepository;

/**
 * Use case for handling element combinations in the Infinite Craft game.
 * <p>
 * This class provides methods to retrieve or create new elements based on combinations
 * of two input elements. It interacts with the repositories to manage element data
 * and combinations.
 * </p>
 *
 * @author Jonathan Hildebrandt
 */
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
     * Inserts a new combination into the repository.
     * This method is called when a new element is generated and needs to be associated
     * with the input elements.
     *
     * @param element1 The first input element.
     * @param element2 The second input element.
     * @param outputElement The resulting output element from the combination.
     * @param callback A callback to be executed after the combination is inserted.
     */
    private void insertCombination(String element1, String element2,
                                   ElementEntity outputElement,
                                   Consumer<ElementEntity> callback) {
        combinationRepository.insertCombination(new CombinationEntity(
                        element1, element2, outputElement.id),
                c -> {
                    Log.d(TAG, "Combination inserted for: "
                            + element1 + " + " + element2);
                    callback.accept(outputElement);
                });
    }

    /**
     * Handles the generation of a new element based on two input elements.
     * It checks if the new element already exists in the repository.
     * If it does, it retrieves the existing element; otherwise, it inserts the new element.
     *
     * @param element1 The first input element.
     * @param element2 The second input element.
     * @param newElement The newly generated ElementEntity to be processed.
     * @param callback A callback to be executed with the resulting ElementEntity.
     */
    private void handleGenerateNew(String element1, String element2, ElementEntity newElement,
                                   Consumer<ElementEntity> callback) {
        Log.d(TAG, "Generated new element: "
                + newElement.emoji + " " + newElement.output);

        // Check if the new element already exists in the repository
        elementRepository.findByName(newElement.output,
                existingElement -> {
                    // If the element exists, return it via the callback
                    if (existingElement != null) {
                        Log.d(TAG, "Element already exists: "
                                + existingElement.emoji + " " + existingElement.output);

                        insertCombination(element1, element2, existingElement, callback);
                    } else {
                        Log.d(TAG, "Element does not exist, inserting new element: "
                                + newElement.emoji + " " + newElement.output);

                        // If the element does not exist, insert
                        // it and create a new combination
                        elementRepository.insertElement(newElement,
                                insertedElement -> {
                                    Log.d(TAG, "Inserted new element with ID: "
                                            + insertedElement.id);
                                    insertCombination(element1, element2,
                                            insertedElement, callback);
                                });
                    }
                });
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

                    // If no combination exists, generate a new element
                    } else {
                        Log.d(TAG, "No combination found for combination: "
                                + element1 + " + " + element2);

                        elementRepository.generateNew(element1, element2,
                                newElement -> handleGenerateNew(element1, element2, newElement, callback));
                    }
                });
    }
}
