package de.thm.mixit.ui.viewmodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import de.thm.mixit.data.entity.Element;
import de.thm.mixit.data.entity.GameState;
import de.thm.mixit.data.entity.Statistic;
import de.thm.mixit.data.model.ElementChip;
import de.thm.mixit.data.model.Result;
import de.thm.mixit.domain.usecase.CombinationUseCase;
import de.thm.mixit.domain.usecase.GameStateUseCase;
import de.thm.mixit.util.LiveDataTestUtil;

/**
 * Tests for {@link GameViewModel}
 * @author Josia Menger
 */
@RunWith(MockitoJUnitRunner.class)
public class GameViewModelTest {

    /**
     * Important so that all architecture components (such as LiveData, ViewModel and Room)
     * execute their tasks immediately and synchronously
     */
    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    @Mock
    private CombinationUseCase mockCombinationUseCase;
    @Mock
    private GameStateUseCase mockGameStateUseCase;

    private GameViewModel viewModel;

    @Before
    public void setup() {
        mockGameStateUseCaseGetAllElements(Arrays.asList(
                new Element("Wasser", "\uD83D\uDCA7"),
                new Element("Erde", "\uD83C\uDF0D"),
                new Element("Feuer", "\uD83D\uDD25"),
                new Element("Luft", "\uD83C\uDF2C️")));
        viewModel = new GameViewModel(mockCombinationUseCase, mockGameStateUseCase);
        mockGameStateRepositoryLoad();
        viewModel.load();
    }

    @Test
    public void loadElements_onInit_callsRepository() {
        verify(mockGameStateUseCase, times(1)).getAllElements(any());
        List<Element> elements = viewModel.getElements().getValue();
        assertNotNull(elements);
        assertEquals(4, elements.size());
    }

    @Test
    public void filter_WithMatchingSearchQuery_ReturnsFilteredElements()
            throws InterruptedException {
        viewModel.onSearchQueryChanged("Was");
        List<Element> filtered = LiveDataTestUtil.getOrAwaitValue(viewModel.getFilteredElements());

        assertEquals(1, filtered.size());
        assertEquals("Wasser", filtered.get(0).name);
    }

    @Test
    public void filter_WithEmptyElementList_ReturnsEmptyFilteredList() throws InterruptedException {
        mockGameStateUseCaseGetAllElements(Collections.emptyList());
        viewModel.loadElements();

        viewModel.onSearchQueryChanged("Was");
        List<Element> filtered = LiveDataTestUtil.getOrAwaitValue(viewModel.getFilteredElements());

        assertTrue(filtered.isEmpty());
    }

    @Test
    public void filter_WithEmptyQuery_ReturnsAllElements()
            throws InterruptedException {
        viewModel.onSearchQueryChanged("");
        List<Element> filtered = LiveDataTestUtil.getOrAwaitValue(viewModel.getFilteredElements());
        List<Element> allElements = LiveDataTestUtil.getOrAwaitValue(viewModel.getElements());

        assertTrue(filtered.containsAll(allElements));
    }


    @Test
    public void addElementToPlayground_WithValidElement_AddsElementToPlayground()
            throws InterruptedException {
        Element element = new Element("Feuer", "\uD83D\uDD25");
        viewModel.addElementToPlayground(element);

        List<ElementChip> result =
                LiveDataTestUtil.getOrAwaitValue(viewModel.getElementsOnPlayground());

        assertEquals(1, result.size());
        assertEquals("Feuer", result.get(0).getElement().name);
    }

    @Test
    public void updateElementPositionOnPlayground_WithValidChip_UpdatesPositionCorrectly()
            throws InterruptedException {
        ElementChip elementChip1 =
                new ElementChip(new Element("Feuer", "\uD83D\uDD25"), 0, 0);
        ElementChip elementChip2 =
                new ElementChip(new Element("Wasser", "\uD83D\uDCA7"), 5, 17);
        viewModel.addElementToPlayground(elementChip1);
        viewModel.addElementToPlayground(elementChip2);

        viewModel.updateElementPositonOnPlayground(elementChip1, 10, 20);
        List<ElementChip> result =
                LiveDataTestUtil.getOrAwaitValue(viewModel.getElementsOnPlayground());

        assertEquals(2, result.size());
        assertEquals(10, result.get(0).getX(), 0.01);
        assertEquals(20, result.get(0).getY(), 0.01);
        assertEquals(5, result.get(1).getX(), 0.01);
        assertEquals(17, result.get(1).getY(), 0.01);

    }

    @Test
    public void combineElements_WithValidReactants_AddsProductToPlayground()
            throws InterruptedException {
        ElementChip chip1 = new ElementChip(new Element("Wasser", "\uD83D\uDCA7"));
        ElementChip chip2 = new ElementChip(new Element("Erde", "\uD83C\uDF0D"));
        viewModel.addElementToPlayground(chip1);
        viewModel.addElementToPlayground(chip2);

        Element resultElement = new Element("Pflanze", "\uD83C\uDF31");
        mockElementUseCaseGetElement(Result.success(resultElement));

        viewModel.combineElements(chip1, chip2);
        List<ElementChip> result =
                LiveDataTestUtil.getOrAwaitValue(viewModel.getElementsOnPlayground());
        Throwable error = LiveDataTestUtil.getOrAwaitValue(viewModel.getError());

        assertNull(error);
        assertEquals(1, result.size());
        assertEquals("Pflanze", result.get(0).getElement().name);
    }

    @Test
    public void combineElements_WithFailedCombination_SetErrorAndAbortCombination()
        throws InterruptedException {
        ElementChip chip1 = new ElementChip(new Element("Wasser", "\uD83D\uDCA7"));
        ElementChip chip2 = new ElementChip(new Element("Erde", "\uD83C\uDF0D"));
        viewModel.addElementToPlayground(chip1);
        viewModel.addElementToPlayground(chip2);

        mockElementUseCaseGetElement(Result.failure(new Throwable("An error occurred")));

        viewModel.combineElements(chip1, chip2);
        List<ElementChip> result =
                LiveDataTestUtil.getOrAwaitValue(viewModel.getElementsOnPlayground());
        Throwable error = LiveDataTestUtil.getOrAwaitValue(viewModel.getError());

        assertEquals("An error occurred", error.getMessage());
        assertEquals(2, result.size());
        assertEquals("Wasser", result.get(0).getElement().name);
        assertEquals("Erde", result.get(1).getElement().name);
    }

    @Test
    public void removeElementFromPlayground_WithValidElement_RemoveOnlyGivenElement()
            throws InterruptedException {
        ElementChip chip1 = new ElementChip(new Element("Wasser", "\uD83D\uDCA7"));
        ElementChip chip2 = new ElementChip(new Element("Erde", "\uD83C\uDF0D"));
        viewModel.addElementToPlayground(chip1);
        viewModel.addElementToPlayground(chip2);

        viewModel.removeElementFromPlayground(chip2);
        List<ElementChip> result =
                LiveDataTestUtil.getOrAwaitValue(viewModel.getElementsOnPlayground());

        assertEquals(1, result.size());
        assertEquals("Wasser", result.get(0).getElement().name);
    }

    @Test
    public void clearPlayground_WhenCalled_RemovesAllElements() throws InterruptedException {
        viewModel.addElementToPlayground(new Element("Wasser", "\uD83D\uDCA7"));
        viewModel.clearPlayground();

        List<ElementChip> result =
                LiveDataTestUtil.getOrAwaitValue(viewModel.getElementsOnPlayground());

        assertTrue(result.isEmpty());
    }

    private void mockGameStateUseCaseGetAllElements(List<Element> list) {
        doAnswer(invocation -> {
            Consumer<List<Element>> callback = invocation.getArgument(0);
            callback.accept(list);
            return null;
        }).when(mockGameStateUseCase).getAllElements(any());
    }

    private void mockElementUseCaseGetElement(Result<Element> result) {
        doAnswer(invocation -> {
            Consumer<Result<Element>> callback = invocation.getArgument(2);
            callback.accept(result);
            return null;
        }).when(mockCombinationUseCase).getElement(any(), any(), any());
    }

    private void mockGameStateRepositoryLoad() {
        GameState dummyGameState = new GameState(0, 0, new String[0], new ArrayList<>());
        Statistic dummyStatistics = new Statistic(0,0, "Wasser", 0, 0,0,0,
                0,0,0, false, new ArrayList<>());

        doAnswer(invocation -> dummyGameState)
                .when(mockGameStateUseCase).load(any());
        doAnswer(invocation -> dummyStatistics)
                .when(mockGameStateUseCase).getStatistics();
    }
}
