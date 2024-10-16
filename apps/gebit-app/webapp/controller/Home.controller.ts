import MessageBox from "sap/m/MessageBox";
import Controller from "sap/ui/core/mvc/Controller";
import AppComponent from "../Component";
import UIComponent from "sap/ui/core/UIComponent";
import { Router$RouteMatchedEvent } from "sap/ui/core/routing/Router";
import Component from "../Component";
import Control from "sap/ui/core/Control";

/**
 * @namespace ui5.gebit.app.controller
 */
export default class Home extends Controller {

	private route:any = "home";

	public onInit() : void {

		let router = (this.getOwnerComponent() as UIComponent).getRouter();
		router.attachRouteMatched(this.attachRouteMatched, this);
	}

	public attachRouteMatched(oEvent:Router$RouteMatchedEvent) {
		let routeName = oEvent.getParameter("name");
		if(routeName == "home") {
			this.homeMatched();
		}
	}

	public homeMatched() {
		this.getView()?.byId("homePage")?.bindElement("/LoggedInUser",);
		(sap.ui.getCore().byId("container-ui5.gebit.app---app--toolHeaderImage") as Control).setVisible(true);
		(sap.ui.getCore().byId("container-ui5.gebit.app---app--toolHeaderSandwichIcon-img") as Control).setVisible(true);
		
	}

	public navToRegister(oEvent:any) {
		(this.getOwnerComponent() as UIComponent).getRouter().navTo("registration");
	}

	public isAdmin(role:string) {
		return role === 'admin';
	}
}