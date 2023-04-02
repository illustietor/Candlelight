package net.satisfy.candlelight.client.screen;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.recipebook.*;
import net.minecraft.client.gui.widget.ToggleButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.recipe.Recipe;
import net.satisfy.candlelight.client.gui.handler.CookingPanScreenHandler;
import net.satisfy.candlelight.client.recipebook.VineryRecipeBookGroup;
import net.satisfy.candlelight.client.recipebook.VineryRecipeBookWidget;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;

@Environment(EnvType.CLIENT)
public class VineryRecipeBookResults {
    private final List<VineryAnimatedResultButton> resultButtons = Lists.newArrayListWithCapacity(20);
    @Nullable
    private VineryAnimatedResultButton hoveredResultButton;
    private final VineryRecipeAlternativesWidget alternatesWidget = new VineryRecipeAlternativesWidget();
    private MinecraftClient client;
    private final List<RecipeDisplayListener> recipeDisplayListeners = Lists.newArrayList();
    private List<VineryRecipeResultCollection> resultCollections = ImmutableList.of();
    private ToggleButtonWidget nextPageButton;
    private ToggleButtonWidget prevPageButton;
    private int pageCount;
    private int currentPage;
    @Nullable
    private Recipe<?> lastClickedRecipe;
    @Nullable
    private VineryRecipeResultCollection resultCollection;
    VineryRecipeBookGroup group;
    private CookingPanScreenHandler cookingPanScreenHandler;

    public VineryRecipeBookResults() {
        for(int i = 0; i < 20; ++i) {
            this.resultButtons.add(new VineryAnimatedResultButton());
        }

    }

    public void initialize(MinecraftClient client, int parentLeft, int parentTop, CookingPanScreenHandler cookingPanScreenHandler) {
        this.client = client;
        this.cookingPanScreenHandler = cookingPanScreenHandler;

        for(int i = 0; i < this.resultButtons.size(); ++i) {
            this.resultButtons.get(i).setPos(parentLeft + 11 + 25 * (i % 5), parentTop + 31 + 25 * (i / 5));
        }

        this.nextPageButton = new ToggleButtonWidget(parentLeft + 93, parentTop + 137, 12, 17, false);
        this.nextPageButton.setTextureUV(1, 208, 13, 18, VineryRecipeBookWidget.TEXTURE);
        this.prevPageButton = new ToggleButtonWidget(parentLeft + 38, parentTop + 137, 12, 17, true);
        this.prevPageButton.setTextureUV(1, 208, 13, 18, VineryRecipeBookWidget.TEXTURE);
    }

    public void setGui(VineryRecipeBookWidget widget) {
        this.recipeDisplayListeners.remove(widget);
        this.recipeDisplayListeners.add(widget);
    }

    public void setResults(List<VineryRecipeResultCollection> resultCollections, boolean resetCurrentPage, VineryRecipeBookGroup group) {
        this.resultCollections = resultCollections;
        this.group = group;
        this.pageCount = (int)Math.ceil((double)resultCollections.size() / 20.0);
        if (this.pageCount <= this.currentPage || resetCurrentPage) {
            this.currentPage = 0;
        }

        this.refreshResultButtons();
    }

    private void refreshResultButtons() {
        int i = 20 * this.currentPage;

        for(int j = 0; j < this.resultButtons.size(); ++j) {
            VineryAnimatedResultButton animatedResultButton = this.resultButtons.get(j);
            if (i + j < this.resultCollections.size()) {
                VineryRecipeResultCollection recipeResultCollection = this.resultCollections.get(i + j);
                animatedResultButton.showResultCollection(recipeResultCollection, cookingPanScreenHandler);
                animatedResultButton.visible = true;
            } else {
                animatedResultButton.visible = false;
            }
        }

        this.hideShowPageButtons();
    }

    private void hideShowPageButtons() {
        this.nextPageButton.visible = this.pageCount > 1 && this.currentPage < this.pageCount - 1;
        this.prevPageButton.visible = this.pageCount > 1 && this.currentPage > 0;
    }

    public void draw(MatrixStack matrices, int x, int y, int mouseX, int mouseY, float delta) {
        if (this.pageCount > 1) {
            int var10000 = this.currentPage + 1;
            String string = "" + var10000 + "/" + this.pageCount;
            int i = this.client.textRenderer.getWidth(string);
            this.client.textRenderer.draw(matrices, string, (float)(x - i / 2 + 73), (float)(y + 141), -1);
        }

        this.hoveredResultButton = null;

        for (VineryAnimatedResultButton animatedResultButton : this.resultButtons) {
            animatedResultButton.render(matrices, mouseX, mouseY, delta);
            if (animatedResultButton.visible && animatedResultButton.isHovered()) {
                this.hoveredResultButton = animatedResultButton;
            }
        }

        this.prevPageButton.render(matrices, mouseX, mouseY, delta);
        this.nextPageButton.render(matrices, mouseX, mouseY, delta);
        this.alternatesWidget.render(matrices, mouseX, mouseY, delta);
    }

    public void drawTooltip(MatrixStack matrices, int x, int y) {
        if (this.client.currentScreen != null && this.hoveredResultButton != null && !this.alternatesWidget.isVisible()) {
            this.client.currentScreen.renderTooltip(matrices, this.hoveredResultButton.getTooltip(this.client.currentScreen), x, y);
        }

    }

    @Nullable
    public Recipe<?> getLastClickedRecipe() {
        return this.lastClickedRecipe;
    }

    @Nullable
    public VineryRecipeResultCollection getLastClickedResults() {
        return this.resultCollection;
    }

    public void hideAlternates() {
        this.alternatesWidget.setVisible(false);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button, int areaLeft, int areaTop, int areaWidth, int areaHeight) {
        this.lastClickedRecipe = null;
        this.resultCollection = null;
        if (this.alternatesWidget.isVisible()) {
            if (this.alternatesWidget.mouseClicked(mouseX, mouseY, button)) {
                this.lastClickedRecipe = this.alternatesWidget.getLastClickedRecipe();
                this.resultCollection = this.alternatesWidget.getResults();
            } else {
                this.alternatesWidget.setVisible(false);
            }

            return true;
        } else if (this.nextPageButton.mouseClicked(mouseX, mouseY, button)) {
            ++this.currentPage;
            this.refreshResultButtons();
            return true;
        } else if (this.prevPageButton.mouseClicked(mouseX, mouseY, button)) {
            --this.currentPage;
            this.refreshResultButtons();
            return true;
        } else {
            Iterator<VineryAnimatedResultButton> var10 = this.resultButtons.iterator();

            VineryAnimatedResultButton animatedResultButton;
            do {
                if (!var10.hasNext()) {
                    return false;
                }

                animatedResultButton = var10.next();
            } while(!animatedResultButton.mouseClicked(mouseX, mouseY, button));

            if (button == 0) {
                this.lastClickedRecipe = animatedResultButton.currentRecipe();
                System.out.println(lastClickedRecipe);
                this.resultCollection = animatedResultButton.getResultCollection();
                System.out.println(resultCollection);
            } else if (button == 1 && !this.alternatesWidget.isVisible() && animatedResultButton.hasResult()) {
                // Implement maybe later
                this.alternatesWidget.showAlternativesForResult(this.client, animatedResultButton.getResultCollection(), animatedResultButton.x, animatedResultButton.y, areaLeft + areaWidth / 2, areaTop + 13 + areaHeight / 2, (float)animatedResultButton.getWidth());
            }

            return true;
        }
    }

    public MinecraftClient getClient() {
        return this.client;
    }
}