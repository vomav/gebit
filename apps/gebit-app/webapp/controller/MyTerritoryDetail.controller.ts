import Controller from "sap/ui/core/mvc/Controller";
import UIComponent from "sap/ui/core/UIComponent";
import { Router$RouteMatchedEvent } from "sap/ui/core/routing/Router";
import Event from "sap/ui/base/Event";
import JSONModel from "sap/ui/model/json/JSONModel";
import ODataModel from "sap/ui/model/odata/v4/ODataModel";
import Dialog from "sap/m/Dialog";
import Filter from "sap/ui/model/Filter";
import FilterOperator from "sap/ui/model/FilterOperator";
import MessageBox from "sap/m/MessageBox";
import Context from "sap/ui/model/odata/v4/Context";
/**
 * @namespace ui5.gebit.app.controller
 */           
export default class MyTerritoryDetail extends Controller {

	public onInit() : void {
		let router = (this.getOwnerComponent() as UIComponent).getRouter();
		router.attachRouteMatched(this.attachRouteMatched, this);
	}

	public attachRouteMatched(oEvent:Router$RouteMatchedEvent) {
		let routeName = oEvent.getParameter("name");
		if(routeName == "myTerritoryDetail") {
			this.matched(oEvent.getParameter("arguments").id);
		}
	}

	public matched(context:string) {
		(this.getView() as any).bindElement("/TerritoryAssignments("+context+")");
		
	}
}