import Controller from "sap/ui/core/mvc/Controller";
import AppComponent from "../Component";
import View from "sap/ui/core/mvc/View";
/**
 * @namespace ui5.gebit.app.controller
 */           
export default class MyTerritoryWorklist extends Controller {

	public onInit() : void {
		const view = this.getView() as View
		if (view) {
			view.addStyleClass((this.getOwnerComponent() as AppComponent).getContentDensityClass());
		}
	}

	

}