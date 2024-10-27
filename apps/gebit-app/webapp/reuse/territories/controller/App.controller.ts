import Controller from "sap/ui/core/mvc/Controller";
import AppComponent from "../Component";
import View from "sap/ui/core/mvc/View";

/**
 * @namespace ui5.gebit.app.reuse.territories.controller
 */           
export default class App extends Controller {

	public onInit() : void {
		// apply content density mode to root view
		const view = this.getView() as View
		if (view) {
			view.addStyleClass((this.getOwnerComponent() as AppComponent).getContentDensityClass());
		}
	}
}