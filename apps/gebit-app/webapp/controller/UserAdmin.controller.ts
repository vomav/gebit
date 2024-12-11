import MessageBox from "sap/m/MessageBox";
import Controller from "sap/ui/core/mvc/Controller";
import AppComponent from "../Component";
import UIComponent from "sap/ui/core/UIComponent";
import { Router$RouteMatchedEvent } from "sap/ui/core/routing/Router";
import Component from "../Component";
import Control from "sap/ui/core/Control";
import { ObjectBindingInfo } from "sap/ui/base/ManagedObject";
import { URLHelper } from "sap/m/library";

/**
 * @namespace ui5.gebit.app.controller
 */
export default class UserAdmin extends Controller {

	private route:any = "home";

	public onInit() : void {

		let router = (this.getOwnerComponent() as UIComponent).getRouter();
		router.attachRouteMatched(this.attachRouteMatched, this);
	}

	public attachRouteMatched(oEvent:Router$RouteMatchedEvent) {
		let routeName = oEvent.getParameter("name");
		if(routeName == "userAdmin") {
			this.homeMatched();
		}
	}

	public homeMatched() {
		let bc = {} as ObjectBindingInfo;
		bc.model="userAdminModel";
		bc.path="/LoggedInUser";
		this.getView()?.bindElement(bc);
	}

	
}