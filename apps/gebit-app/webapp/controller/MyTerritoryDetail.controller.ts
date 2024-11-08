import Controller from "sap/ui/core/mvc/Controller";
import UIComponent from "sap/ui/core/UIComponent";
import { Router$RouteMatchedEvent } from "sap/ui/core/routing/Router";
import Event from "sap/ui/base/Event";
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

	public onActionButtonPress(oEvent:Event) {
		var oButton = oEvent.getSource();
		this.getView().byId("myPartsactionSheet").openBy(oButton);
	}
}