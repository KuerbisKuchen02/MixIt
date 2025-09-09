package de.thm.mixit.domain.logic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import androidx.recyclerview.widget.DiffUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import de.thm.mixit.data.entity.Element;
import de.thm.mixit.data.model.ElementChip;

/**
 * Tests for {@link GenericListUpdateCallback}
 * @author Josia Menger
 */
@RunWith(MockitoJUnitRunner.class)
public class GenericListUpdateCallbackTest {

    private List<ElementChip> workingList;
    @Mock
    private TestHandler mockHandler;

    @Before
    public void setUp() {
        workingList = new ArrayList<>(List.of(
                new ElementChip(new Element("Wasser", "\uD83D\uDCA7"), 0, 0),
                new ElementChip(new Element("Feuer", "\uD83D\uDD25"), 0, 0)
        ));
    }

    @Test
    public void onInserted_WithNewElement_ReturnsNullPlaceHolderInWorkingList() {
        List<ElementChip> expected = new ArrayList<>(workingList);
        expected.add(new ElementChip(new Element("Erde", "\uD83C\uDF0D")));

        dispatchUpdatesToResult(expected);

        verifyNoInteractions(mockHandler);
        assertEquals(3, workingList.size());
        assertNull(workingList.get(2));
    }

    @Test
    public void onRemoved_WithValidElement_ReturnsResultWithoutElement() {
        List<ElementChip> expected = new ArrayList<>(workingList);
        expected.remove(0);

        dispatchUpdatesToResult(expected);

        verifyWantedNumbersOfInvocations(0, 1, 0);
        assertEquals(1, workingList.size());
        assertEquals("Feuer", workingList.get(0).getElement().name);
    }

    @Test
    public void onChanged_WithNewPosition_ReturnsResultsWithElementWithNewPosition() {
        List<ElementChip> expected = new ArrayList<>(workingList);
        ElementChip oldElement = expected.get(0);
        ElementChip newElement = new ElementChip(oldElement.getId(), oldElement.getElement(),
                oldElement.getX(), 10);
        expected.set(0, newElement);

        dispatchUpdatesToResult(expected);

        verifyWantedNumbersOfInvocations(0, 0, 1);
        assertEquals(0, workingList.get(0).getX(), 0.01);
        assertEquals(10, workingList.get(0).getY(), 0.01);
    }

    @Test
    public void onMoved_WithSwappedElementPositions_ReturnsWorkingListCorrectlyUpdated() {
        List<ElementChip> expected = new ArrayList<>(workingList);
        ElementChip temp = expected.get(0);
        expected.set(0, expected.get(1));
        expected.set(1, temp);

        dispatchUpdatesToResult(expected);

        verifyNoInteractions(mockHandler);
        assertEquals("Feuer", workingList.get(0).getElement().name);
        assertEquals("Wasser", workingList.get(1).getElement().name);
    }

    @Test
    public void finishInserts_WithNewElement_ReturnsNewElementInWorkingList() {
        List<ElementChip> expected = new ArrayList<>(workingList);
        ElementChip newElement = new ElementChip(new Element("Erde", "\uD83C\uDF0D"));
        expected.add(1, newElement);

        GenericListUpdateCallback<ElementChip> callback = dispatchUpdatesToResult(expected);
        callback.finishInserts();

        verifyWantedNumbersOfInvocations(1, 0, 0);
        assertEquals(3, workingList.size());
        assertEquals("Erde", workingList.get(1).getElement().name);
        assertEquals("Feuer", workingList.get(2).getElement().name);
    }

    @Test
    public void genericListUpdate_AddAndRemoveElements_ReturnsUpdatedWorkingList() {
        List<ElementChip> expected = new ArrayList<>();
        ElementChip newElement = new ElementChip(new Element("Erde", "\uD83C\uDF0D"));
        expected.add(newElement);

        GenericListUpdateCallback<ElementChip> callback = dispatchUpdatesToResult(expected);
        callback.finishInserts();

        verifyWantedNumbersOfInvocations(1, 2, 0);
        assertEquals(1, workingList.size());
        assertEquals("Erde", workingList.get(0).getElement().name);
    }

    private GenericListUpdateCallback<ElementChip> dispatchUpdatesToResult(
            List<ElementChip> expected) {
        DiffUtil.DiffResult diffResult =
                DiffUtil.calculateDiff(new ElementDiffCallback(workingList, expected));
        GenericListUpdateCallback<ElementChip> callback =
                new GenericListUpdateCallback<>(workingList, expected, mockHandler);
        diffResult.dispatchUpdatesTo(callback);
        return callback;
    }

    private void verifyWantedNumbersOfInvocations(int inserted, int removed, int changed) {
        verify(mockHandler, times(inserted)).onItemInserted(any(), anyInt());
        verify(mockHandler, times(removed)).onItemRemoved(any(), anyInt());
        verify(mockHandler, times(changed)).onItemChanged(any(), anyInt());
    }

    /**
     * Has no functionality, just used to count invocations
     */
    static class TestHandler implements GenericListChangeHandler<ElementChip> {

        @Override
        public void onItemInserted(ElementChip item, int position) {}

        @Override
        public void onItemRemoved(ElementChip item, int position) {}

        @Override
        public void onItemChanged(ElementChip item, int position) {}
    }
}
