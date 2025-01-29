package fl205.advancedtrommels;

import turniplabs.halplibe.helper.RecipeBuilder;
import turniplabs.halplibe.util.RecipeEntrypoint;

import static fl205.advancedtrommels.AdvancedTrommels.MOD_ID;

public class AdvancedTrommelsRecipes implements RecipeEntrypoint {

	@Override
	public void onRecipesReady() {

	}

	@Override
	public void initNamespaces() {
		RecipeBuilder.initNameSpace(MOD_ID);
	}
}
