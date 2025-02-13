import Controller from "sap/ui/core/mvc/Controller";
import AppComponent from "../Component";
import View from "sap/ui/core/mvc/View";
import ColumnListItem from "sap/m/ColumnListItem";
import UIComponent from "sap/ui/core/UIComponent";
import { Router$RouteMatchedEvent } from "sap/ui/core/routing/Router";
/**
 * @namespace ui5.gebit.app.controller
 */           
export default class GroupTerritoriesWorklist extends Controller {

	isModelInitialized:Boolean;
	public onInit() : void {
		const view = this.getView() as View
		if (view) {
			view.addStyleClass((this.getOwnerComponent() as AppComponent).getContentDensityClass());
		}

		let router = (this.getOwnerComponent() as UIComponent).getRouter();
		router.attachRouteMatched(this.attachRouteMatched, this);
	}


	public attachRouteMatched(oEvent:Router$RouteMatchedEvent) {
		let routeName = oEvent.getParameter("name");
		if(routeName == "groupTerritories" && this.isModelInitialized) {
			this.getView()?.getModel()?.refresh();
		}
		this.isModelInitialized = true;
	}
	public onPressListItem(oEvent:Event) {
		let oBindingContext = (oEvent.getSource() as ColumnListItem).getBindingContext();
		let router = (this.getOwnerComponent() as UIComponent).getRouter();
		router.navTo("groupTerritoryDetail",{
			"id" : oBindingContext?.getProperty("ID")
		})
	}

	public formatStartEndDate(value:string) {
		let date = new Date(value);
		var d = date.getDate();
		var m = date.getMonth() + 1; //Month from 0 to 11
		var y = date.getFullYear();
		return '' + y + '-' + (m<=9 ? '0' + m : m) + '-' + (d <= 9 ? '0' + d : d);
	}
}