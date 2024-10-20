import Controller from "sap/ui/core/mvc/Controller";
import UIComponent from "sap/ui/core/UIComponent";
import Control from "sap/ui/core/Control";
import { Router$RouteMatchedEvent } from "sap/ui/core/routing/Router";
import Event from "sap/ui/base/Event";
import JSONModel from "sap/ui/model/json/JSONModel";
/**
 * @namespace ui5.gebit.app.reuse.territories.controller
 */           
export default class Details extends Controller {

	public onInit() : void {
		let router = (this.getOwnerComponent() as UIComponent).getRouter();
		router.attachRouteMatched(this.attachRouteMatched, this);
	}

	public attachRouteMatched(oEvent:Router$RouteMatchedEvent) {
		let routeName = oEvent.getParameter("name");
		if(routeName == "detail") {
			this.matched(oEvent.getParameter("arguments").id);
		}
	}

	public matched(context:string) {
		(this.getView() as any).bindElement("/Territories("+context+")");
		
	}

	public onEditButton(oEvent:Event) {
		(this.getView()?.getModel("uiModel") as JSONModel).setProperty("/ui/editMode", true);
	}

	public onDisplayMode(oEvent:Event) {
		(this.getView()?.getModel("uiModel") as JSONModel).setProperty("/ui/editMode", false);
	}

}