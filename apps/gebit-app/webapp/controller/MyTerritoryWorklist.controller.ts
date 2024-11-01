import Controller from "sap/ui/core/mvc/Controller";
import AppComponent from "../Component";
import View from "sap/ui/core/mvc/View";
import ColumnListItem from "sap/m/ColumnListItem";
import UIComponent from "sap/ui/core/UIComponent";
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

	
	public onPressListItem(oEvent:Event) {
		let oBindingContext = (oEvent.getSource() as ColumnListItem).getBindingContext();
		let router = (this.getOwnerComponent() as UIComponent).getRouter();
		router.navTo("myTerritoryDetail",{
			"id" : oBindingContext?.getProperty("ID")
		})
	}
}