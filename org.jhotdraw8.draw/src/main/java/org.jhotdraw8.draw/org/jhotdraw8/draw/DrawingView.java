/*
 * @(#)DrawingView.java
 * Copyright © 2022 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlySetProperty;
import javafx.collections.ObservableSet;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.transform.Transform;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.beans.NonNullObjectProperty;
import org.jhotdraw8.draw.constrain.Constrainer;
import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.Figures;
import org.jhotdraw8.draw.handle.Handle;
import org.jhotdraw8.draw.input.ClipboardInputFormat;
import org.jhotdraw8.draw.input.ClipboardOutputFormat;
import org.jhotdraw8.draw.model.DrawingModel;
import org.jhotdraw8.draw.render.WritableRenderContext;
import org.jhotdraw8.draw.tool.Tool;
import org.jhotdraw8.geom.FXTransforms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

/**
 * A {@code DrawingView} can display a {@code Drawing} in a JavaFX scene graph.
 * <p>
 * A {@code DrawingView} consists of the following layers:
 * <ul>
 * <li>Background. Displays a background behind the drawing.</li>
 * <li>Drawing. Displays the figures of the drawing.</li>
 * <li>Grid. Displays a grid.</li>
 * <li>Tools. Displays the handles used for editing figures.</li>
 * </ul>
 * <p>
 * {@code DrawingView} uses a {@code DrawingModel} to listen for changes on a
 * {@code Drawing}. Once a drawing is showing in a drawing view, all changes to
 * the drawing must be performed on the drawing model.</p>
 * <p>
 * {@code DrawingView} invokes {@code validate()} on its {@code DrawingModel}
 * each time before it renders the drawing to ensure that the figures are laid
 * out and that CSS styles are applied before rendering the drawing.
 * </p>
 *
 * @author Werner Randelshofer
 * Responsibility, Handler.
 */
public interface DrawingView extends WritableRenderContext {

    // ---
    // property names
    // ----
    /**
     * The name of the model property.
     */
    String MODEL_PROPERTY = "model";
    /**
     * The name of the tool property.
     */
    String TOOL_PROPERTY = "tool";
    /**
     * The name of the focused property.
     */
    String FOCUSED_PROPERTY = "focused";
    /**
     * The name of the scale factor property.
     */
    String ZOOM_FACTOR_PROPERTY = "scaleFactor";
    /**
     * The name of the constrainer property.
     */
    String CONSTRAINER_PROPERTY = "constrainer";
    /**
     * The name of the selection property.
     */
    String SELECTED_FIGURES_PROPERTY = "selectedFigures";
    /**
     * The name of the active handle property.
     */
    String ACTIVE_HANDLE_PROPERTY = "activeHandle";
    /**
     * The name of the active layer property.
     */
    String ACTIVE_PARENT_PROPERTY = "activeLayer";
    /**
     * The name of the clipboardInputFormat property.
     */
    String CLIPBOARD_INPUT_FORMAT_PROPERTY = "clipboardInputFormat";
    /**
     * The name of the clibpoardOutputFormat property.
     */
    String CLIPBOARD_OUTPUT_FORMAT_PROPERTY = "clibpoardOutputFormat";
    /**
     * The name of the drawing property.
     */
    String DRAWING_PROPERTY = "drawing";
    /**
     * The name of the editor property.
     */
    String EDITOR_PROPERTY = "editor";


    // ---
    // properties
    // ---

    /**
     * The drawing model.
     *
     * @return the drawing model property, with {@code getBean()} returning this
     * drawing view, and {@code getName()} returning {@code DRAWING_PROPERTY}.
     */
    @NonNull NonNullObjectProperty<DrawingModel> modelProperty();

    /**
     * The drawing model.
     *
     * @return the drawing model property
     */
    @NonNull ReadOnlyObjectProperty<Drawing> drawingProperty();

    /**
     * The drawing editor.
     *
     * @return the editor property
     */
    @NonNull ObjectProperty<DrawingEditor> editorProperty();

    /**
     * The active layer of the drawing.
     *
     * @return the active layer of the drawing. Returns null if the drawing has
     * no layers or no layer has been activated.
     */
    @NonNull ObjectProperty<Figure> activeParentProperty();

    default void scrollSelectedFiguresToVisible() {
        final ObservableSet<Figure> selectedFigures = getSelectedFigures();
        if (!selectedFigures.isEmpty()) {
            scrollRectToVisible(worldToView(Figures.getBoundsInWorld(selectedFigures)));
        }
    }

    /**
     * The tool which currently edits this {@code DrawingView}.
     * <p>
     * When a tool is set on the drawing view, then drawing view adds the
     * {@code Node} of the tool to its tool panel which is stacked on top of the
     * drawing panel. It then invokes {@code toolsetDrawingView(this)}.
     * <p>
     * Setting a tool will removeChild the previous tool. The drawing view
     * invokes {@code tool.setDrawingView(null)} and then removes its
     * {@code Node} from its tool panel.
     *
     * @return the tool property, with {@code getBean()} returning this drawing
     * view, and {@code getName()} returning {@code TOOL_PROPERTY}.
     */
    @NonNull ObjectProperty<Tool> toolProperty();

    /**
     * The scale factor of the drawing view.
     *
     * @return The zoom factor. The value is always greater than 0. Values
     * larger than 1 cause a magnification. Values between 0 and 1 causes a
     * minification.
     */
    @NonNull DoubleProperty zoomFactorProperty();

    /**
     * The constrainer.
     *
     * @return the constrainer property, with {@code getBean()} returning this
     * drawing view, and {@code getName()} returning
     * {@code CONSTRAINER_PROPERTY}.
     */
    @NonNull NonNullObjectProperty<Constrainer> constrainerProperty();

    /**
     * The focused property is set to true, when the DrawingView has input
     * focus.
     *
     * @return the focused property, with {@code getBean()} returning this
     * drawing view, and {@code getName()} returning {@code FOCUSED_PROPERTY}.
     */
    @NonNull ReadOnlyBooleanProperty focusedProperty();

    /**
     * The selected figures.
     * <p>
     * Note: The selection is represent by a {@code LinkedHasSet} because the
     * sequence of the selection is important.
     *
     * @return a list of the selected figures
     */
    @NonNull ReadOnlySetProperty<Figure> selectedFiguresProperty();

    /**
     * The active handle.
     *
     * @return the active handle if present
     */
    @NonNull ObjectProperty<Handle> activeHandleProperty();


    /**
     * The clipboard output format.
     *
     * @return the clipboard output format handle if present
     */
    @NonNull ObjectProperty<ClipboardOutputFormat> clipboardOutputFormatProperty();

    /**
     * The clipboard input format.
     *
     * @return the clipboard output format handle if present
     */
    @NonNull ObjectProperty<ClipboardInputFormat> clipboardInputFormatProperty();

    // ---
    // methods
    // ---

    /**
     * Returns the {@code javafx.scene.Node} of the DrawingView.
     *
     * @return a node
     */
    Node getNode();

    /**
     * Gets the node which is used to render the specified figure by the drawing
     * view.
     *
     * @param f The figure
     * @return The node associated to the figure
     */
    @Nullable Node getNode(Figure f);

    /**
     * Finds the handle at the given view coordinates. Handles are searched in
     * Z-order from front to back. Skips handles which are not selectable.
     *
     * @param vx x in view coordinates
     * @param vy y in view coordinates
     * @return A handle or null
     */
    @Nullable
    Handle findHandle(double vx, double vy);

    /**
     * Finds the figure at the given view coordinates. Figures are searched in
     * Z-order from front to back. Skips disabled figures.
     *
     * @param vx      x in view coordinates
     * @param vy      y in view coordinates
     * @param figures Only searches in the provided list of figures
     * @return A figure or null
     */
    default @Nullable Figure findFigure(double vx, double vy, Set<Figure> figures) {
        List<Map.Entry<Figure, Double>> result = findFigures(vx, vy, false, figures::contains);
        return getClosestFigure(result);
    }

    default Figure getClosestFigure(List<Map.Entry<Figure, Double>> result) {
        double closestDistance = Double.POSITIVE_INFINITY;
        Figure closestFigure = null;
        for (Map.Entry<Figure, Double> entry : result) {
            if (entry.getValue() < closestDistance) {
                closestDistance = entry.getValue();
                closestFigure = entry.getKey();
                if (closestDistance == 0.0) {
                    break;
                }
            }
        }
        return closestFigure;
    }

    /**
     * Finds the figure at the given view coordinates. Figures are searched in
     * Z-order from front to back. Only returns selectable figures.
     *
     * @param vx x in view coordinates
     * @param vy y in view coordinates
     * @return A figure or null
     */
    default @Nullable Figure findFigure(double vx, double vy) {
        return findFigure(vx, vy, Figure::isSelectable);
    }

    /**
     * Finds the figure at the given view coordinates. Figures are searched in
     * Z-order from front to back.
     *
     * @param vx x in view coordinates
     * @param vy y in view coordinates
     * @return A figure or null
     */
    default @Nullable Figure findFigure(double vx, double vy, Predicate<Figure> predicate) {
        List<Map.Entry<Figure, Double>> result = findFigures(vx, vy, false, predicate);
        return getClosestFigure(result);
    }


    /**
     * Finds the front-most node of the specified figure that contains the
     * specified view coordinates.
     *
     * @param figure the figure
     * @param vx     x in view coordinates
     * @param vy     y in view coordinates
     * @return A node or null
     */
    @Nullable
    Node findFigureNode(@NonNull Figure figure, double vx, double vy);

    /**
     * Finds the figure at the given view coordinates behind the given figure.
     * Figures are searched in Z-order from front to back. Only returns
     * figures that are selectable.
     *
     * @param vx        x in view coordinates
     * @param vy        y in view coordinates
     * @param decompose whether to decompose the figures
     * @return A list of figures from front to back.
     * Each entry contains the figure and the distance of the figure to vx,vy.
     * Distance 0 means that vx,vy is inside the figure.
     */
    default @NonNull List<Map.Entry<Figure, Double>> findFigures(double vx, double vy, boolean decompose) {
        return findFigures(vx, vy, decompose, Figure::isSelectable);
    }

    /**
     * Finds the figure at the given view coordinates behind the given figure.
     * Figures are searched in Z-order from front to back.
     *
     * @param vx        x in view coordinates
     * @param vy        y in view coordinates
     * @param decompose whether to decompose the figures
     * @return A list of figures from front to back.
     * Each entry contains the figure and the distance of the figure to vx,vy.
     * Distance 0 means that vx,vy is inside the figure.
     */
    @NonNull
    List<Map.Entry<Figure, Double>> findFigures(double vx, double vy, boolean decompose, @NonNull Predicate<Figure> predicate);

    /**
     * Returns all figures that lie within the specified bounds given in view
     * coordinates. The figures are returned in Z-order from back to front.
     * Skips disabled figures.
     *
     * @param vx        x in view coordinates
     * @param vy        y in view coordinates
     * @param vwidth    width in view coordinates
     * @param vheight   height in view coordinates
     * @param decompose whether to decompose the figures
     * @return A list of figures from front to back.
     * Each entry contains the figure and the distance of the figure to vx,vy.
     * Distance 0 means that vx,vy is inside the figure.
     */
    @NonNull List<Map.Entry<Figure, Double>> findFiguresInside(double vx, double vy, double vwidth, double vheight, boolean decompose);

    /**
     * Returns all figures that intersect the specified bounds given in view
     * coordinates. The figures are returned in Z-order from front to back.
     * Skips disabled figures.
     *
     * @param vx        x in view coordinates
     * @param vy        y in view coordinates
     * @param vwidth    width in view coordinates
     * @param vheight   height in view coordinates
     * @param decompose whether to decompose the figures
     * @return A list of figures from front to back
     */
    default @NonNull List<Map.Entry<Figure, Double>> findFiguresIntersecting(double vx, double vy, double vwidth, double vheight, boolean decompose) {
        return findFiguresIntersecting(vx, vy, vwidth, vheight, decompose, f -> true);
    }

    /**
     * Returns all figures that intersect the specified bounds given in view
     * coordinates. The figures are returned in Z-order from front to back.
     * Skips disabled figures.
     *
     * @param vx        x in view coordinates
     * @param vy        y in view coordinates
     * @param vwidth    width in view coordinates
     * @param vheight   height in view coordinates
     * @param decompose whether to decompose the figures
     * @param predicate predicate for filtering figures
     * @return A list of figures from front to back
     */
    @NonNull List<Map.Entry<Figure, Double>> findFiguresIntersecting(double vx, double vy, double vwidth, double vheight, boolean decompose, Predicate<Figure> predicate);

    // Handles

    /**
     * Gets selected figures with the same handle.
     *
     * @param figures selected figures
     * @param handle  a handle
     * @return A collection containing the figures with compatible handles.
     */
    @NonNull Set<Figure> getFiguresWithCompatibleHandle(Collection<Figure> figures, Handle handle);

    /**
     * Returns the world to view transformation.
     *
     * @return the transformation
     */
    @NonNull
    Transform getWorldToView();

    /**
     * Returns the view to world transformation.
     *
     * @return the transformation;
     */
    @NonNull
    Transform getViewToWorld();

    // ---
    // convenience methods
    // ---

    /**
     * Finds the figure at the given view coordinates. Figures are searched in
     * Z-order from front to back. Skips disabled figures and non-selectable
     * figures.
     *
     * @param pointInView point in view coordinates
     * @return A figure or empty
     */
    default @Nullable Figure findFigure(@NonNull Point2D pointInView) {
        return findFigure(pointInView.getX(), pointInView.getY());
    }

    /**
     * Finds the figures at the given view coordinates. Figures are searched in
     * Z-order from front to back. Skips disabled figures and non-selectable
     * figures.
     *
     * @param pointInView point in view coordinates
     * @param decompose   whether to decompose the figures
     * @return A list of figures from front to back.
     * Each entry contains the figure and the distance of the figure to vx,vy.
     * Distance 0 means that pointInView is inside the figure.
     */
    default @NonNull List<Map.Entry<Figure, Double>> findFigures(@NonNull Point2D pointInView, boolean decompose) {
        return findFigures(pointInView.getX(), pointInView.getY(), decompose);
    }

    /**
     * Returns all figures that are inside the specified bounds given in view
     * coordinates. The figures are returned in Z-order from front to back.
     * Skips disabled figures and non-selectable figures.
     *
     * @param rectangleInView rectangle in view coordinates
     * @param decompose       whether to decompose the figures
     * @return A list of figures from front to back.
     * Each entry contains the figure and the distance of the figure to vx,vy.
     * Distance 0 means that pointInView is inside the figure.
     */
    default @NonNull List<Map.Entry<Figure, Double>> findFiguresInside(@NonNull Rectangle2D rectangleInView, boolean decompose) {
        return findFiguresInside(rectangleInView.getMinX(), rectangleInView.getMinY(), rectangleInView.getWidth(), rectangleInView.getHeight(), decompose);
    }

    /**
     * Returns all figures that intersect the specified bounds given in view
     * coordinates. The figures are returned in Z-order from back to front.
     * Skips disabled figures and non-selectable figures.
     *
     * @param rectangleInView rectangle in view coordinates
     * @param decompose       whether to decompose the figures
     * @return A list of figures from front to back
     */
    default @NonNull List<Map.Entry<Figure, Double>> findFiguresIntersecting(@NonNull Rectangle2D rectangleInView, boolean decompose) {
        return findFiguresIntersecting(rectangleInView.getMinX(), rectangleInView.getMinY(), rectangleInView.getWidth(), rectangleInView.getHeight(), decompose, Figure::isSelectable);
    }

    default void setDrawing(Drawing newValue) {
        getModel().setRoot(newValue);
        setActiveParent(null);
    }

    default Drawing getDrawing() {
        return modelProperty().get().getDrawing();
    }

    default void setEditor(DrawingEditor newValue) {
        editorProperty().set(newValue);
    }

    default DrawingEditor getEditor() {
        return editorProperty().get();
    }

    default void setConstrainer(Constrainer newValue) {
        constrainerProperty().set(newValue);
    }

    default Constrainer getConstrainer() {
        return constrainerProperty().get();
    }

    default void setTool(@Nullable Tool newValue) {
        toolProperty().set(newValue);
    }

    default @Nullable Tool getTool() {
        return toolProperty().get();
    }

    default void setActiveHandle(@Nullable Handle newValue) {
        activeHandleProperty().set(newValue);
    }

    default @Nullable Handle getActiveHandle() {
        return activeHandleProperty().get();
    }

    default @Nullable Figure getSelectionLead() {
        ArrayList<Figure> selection = new ArrayList<>(getSelectedFigures());
        return selection.isEmpty() ? null : selection.get(selection.size() - 1);
    }

    default @Nullable Figure getSelectionAnchor() {
        Set<Figure> selection = getSelectedFigures();
        return selection.isEmpty() ? null : selection.iterator().next();
    }

    default void setActiveParent(@Nullable Figure newValue) {
        activeParentProperty().set(newValue);
    }

    default @Nullable Figure getActiveParent() {
        return activeParentProperty().get();
    }

    default void setZoomFactor(double newValue) {
        zoomFactorProperty().set(newValue);
    }

    default double getZoomFactor() {
        return zoomFactorProperty().get();
    }

    default ObservableSet<Figure> getSelectedFigures() {
        return selectedFiguresProperty().get();
    }

    /**
     * Converts view coordinates into world coordinates.
     *
     * @param view a point in view coordinates
     * @return the corresponding point in world coordinates
     */
    default Point2D viewToWorld(@NonNull Point2D view) {
        return FXTransforms.transform(getViewToWorld(), view);
    }

    /**
     * Converts view coordinates into world coordinates.
     *
     * @param view a rectangle in view coordinates
     * @return the corresponding point in world coordinates
     */
    default Bounds viewToWorld(@NonNull Bounds view) {
        return getViewToWorld().transform(view);
    }

    /**
     * Converts world coordinates into view coordinates.
     *
     * @param world a point in world coordinates
     * @return the corresponding point in view coordinates
     */
    default Point2D worldToView(@NonNull Point2D world) {
        return FXTransforms.transform(getWorldToView(), world);
    }

    /**
     * Converts world coordinates into view coordinates.
     *
     * @param world a box in world coordinates
     * @return the corresponding box in view coordinates
     */
    default Bounds worldToView(@NonNull Bounds world) {
        return getWorldToView().transform(world);
    }

    /**
     * Converts view coordinates into world coordinates.
     *
     * @param vx the x coordinate of a point in view coordinates
     * @param vy the y coordinate of a point in view coordinates
     * @return the corresponding point in world coordinates
     */
    default Point2D viewToWorld(double vx, double vy) {
        return getViewToWorld().transform(vx, vy);
    }

    /**
     * Converts world coordinates into view coordinates.
     *
     * @param dx the x coordinate of a point in world coordinates
     * @param dy the y coordinate of a point in world coordinates
     * @return the corresponding point in view coordinates
     */
    default Point2D worldToView(double dx, double dy) {
        return getWorldToView().transform(dx, dy);
    }

    /**
     * Returns the underlying drawing model.
     *
     * @return a drawing model
     */
    default DrawingModel getModel() {
        return modelProperty().get();
    }

    /**
     * Sets a new underlying drawing model.
     *
     * @param newValue a drawing model
     */
    default void setModel(DrawingModel newValue) {
        modelProperty().set(newValue);
    }

    default void setClipboardOutputFormat(@Nullable ClipboardOutputFormat newValue) {
        clipboardOutputFormatProperty().set(newValue);
    }

    default void setClipboardInputFormat(@Nullable ClipboardInputFormat newValue) {
        clipboardInputFormatProperty().set(newValue);
    }

    default @Nullable ClipboardOutputFormat getClipboardOutputFormat() {
        return clipboardOutputFormatProperty().get();
    }

    default @Nullable ClipboardInputFormat getClipboardInputFormat() {
        return clipboardInputFormatProperty().get();
    }

    void recreateHandles();

    /**
     * Plays a short animation on the handles to make them easier discoverable.
     */
    void jiggleHandles();

    /**
     * Scrolls the specified figure to visible.
     *
     * @param f A figure in the drawing of this DrawingView.
     */
    default void scrollFigureToVisible(@NonNull Figure f) {
        Bounds boundsInView = worldToView(f.getLayoutBoundsInWorld());
        scrollRectToVisible(boundsInView);
    }

    /**
     * Scrolls the specified rectangle to visible.
     *
     * @param boundsInView A rectangle in view coordinates.
     */
    void scrollRectToVisible(Bounds boundsInView);

    /**
     * Returns the visible rectangle of the drawing view in view coordinates.
     *
     * @return the portion of the DrawingView that is visible on screen.
     */
    Bounds getVisibleRect();


}
