package de.thm.mixit.domain.usecase;

import android.util.Log;

import java.util.function.Consumer;

import de.thm.mixit.data.entity.Combination;
import de.thm.mixit.data.entity.Element;
import de.thm.mixit.data.model.Result;
import de.thm.mixit.data.repository.CombinationRepository;
import de.thm.mixit.data.repository.ElementRepository;

/**
 * Use case for handling element combinations in the Infinite Craft game.
 * <p>
 * This class provides methods to retrieve or create new elements based on combinations
 * of two input elements. It interacts with the repositories to manage element data
 * and combinations.
 *
 * @author Jonathan Hildebrandt
 */
public class CombinationUseCase {

    private static final String TAG = CombinationUseCase.class.getSimpleName();

    private final CombinationRepository combinationRepository;
    private final ElementRepository elementRepository;

    /**
     * Constructor for CombinationUseCase.
     * Initializes the repositories needed for element operations.
     * @param combinationRepository The combination repository
     *                              that is used for managing combinations.
     * @param elementRepository The element repository that is used for managing elements
     */
    public CombinationUseCase(CombinationRepository combinationRepository,
                              ElementRepository elementRepository) {
        this.combinationRepository = combinationRepository;
        this.elementRepository = elementRepository;
    }

    /**
     * Combines two elements to create a new element.
     * If the combination already exists, it retrieves the existing element.
     * Otherwise, it generates a new element and stores the combination.
     * @param element1 The first element to combine.
     * @param element2 The second element to combine.
     * @param callback A callback to receive the resulting ElementEntity.
     * @throws RuntimeException If an error occurs during the operation.
     */
    public void getElement(Element element1, Element element2,
                           Consumer<Result<Element>> callback) throws RuntimeException {
        // Check if there is already a combination for the two elements
        combinationRepository.findByCombination(element1.toString(), element2.toString(),
                combination -> {
                    // If a combination exists, retrieve the output element
                    if (combination != null) {
                        Log.i(TAG, "Combination found for element: "
                                + combination.inputA + " + " + combination.inputB
                                + " with outputId: " + combination.outputId);

                        elementRepository.findById(combination.outputId, element -> {
                            if (element != null) callback.accept(Result.success(element));
                        });
                        return;
                    }

                    // If no combination exists, generate a new element
                    Log.i(TAG, "No combination found for combination: "
                            + element1 + " + " + element2);

                    elementRepository.generateNew(element1.toString(), element2.toString(),
                            result -> {
                        if (result.isError()) {
                            Log.e(TAG, "Failed to generate element: " + result.getError());
                            callback.accept(result);
                            return;
                        }

                        handleGenerateNew(element1, element2, result.getData(), callback);
                    });
                });
    }

    /**
     * Handles the generation of a new element based on two input elements.
     * It checks if the new element already exists in the repository.
     * If it does, it retrieves the existing element; otherwise, it inserts the new element.
     * @param element1 The first input element.
     * @param element2 The second input element.
     * @param newElement The newly generated ElementEntity to be processed.
     * @param callback A callback to be executed with the resulting ElementEntity.
     */
    private void handleGenerateNew(Element element1, Element element2, Element newElement,
                                   Consumer<Result<Element>> callback) {
        Log.i(TAG, "Generated new element: " + newElement.emoji + " " + newElement.name);

        // Check if the new element already exists in the repository
        elementRepository.findByName(newElement.name, existingElement -> {
            // If the element exists, return it via the callback
            if (existingElement != null) {
                Log.i(TAG, "Element already exists: "
                        + existingElement.emoji + " " + existingElement.name);

                insertCombination(element1, element2, existingElement, callback);
                return;
            }
            Log.i(TAG, "Element does not exist, inserting new element: "
                    + newElement.emoji + " " + newElement.name);

            // If the element does not exist, insert it and create a new combination
            elementRepository.insertElement(newElement, insertedElement -> {
                Log.i(TAG, "Inserted new element with ID: " + insertedElement.id);
                insertCombination(element1, element2, insertedElement, callback);
            });
        });
    }

    /**
     * Inserts a new combination into the repository.
     * This method is called when a new element is generated and needs to be associated
     * with the input elements.
     * @param element1 The first input element.
     * @param element2 The second input element.
     * @param outputElement The resulting output element from the combination.
     * @param callback A callback to be executed after the combination is inserted.
     */
    private void insertCombination(Element element1, Element element2,
                                   Element outputElement,
                                   Consumer<Result<Element>> callback) {
        combinationRepository.insertCombination(
                new Combination(element1.toString(), element2.toString(), outputElement.id),
                result -> {
                    if(result.isError()) {
                        Log.w(TAG, "Combination could not be inserted for: "
                                + element1 + " + " + element2);
                        callback.accept(Result.failure(result.getError()));
                    } else {
                        Log.i(TAG, "Combination inserted for: " + element1 + " + " + element2);
                        callback.accept(Result.success(outputElement));
                    }
                });
    }
}
