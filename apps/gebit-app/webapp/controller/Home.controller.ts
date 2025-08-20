import Controller from "sap/ui/core/mvc/Controller";
import UIComponent from "sap/ui/core/UIComponent";
import { Router$RouteMatchedEvent } from "sap/ui/core/routing/Router";
import Control from "sap/ui/core/Control";
import { ObjectBindingInfo } from "sap/ui/base/ManagedObject";

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
		let bi = {} as ObjectBindingInfo;
		bi.path = "/LoggedInUser",
		bi.model = "userAdminModel";
		
		let toolPageControl = sap.ui.getCore().byId("container-ui5.gebit.app---app--toolPage") as Control;
		if(toolPageControl) {
			toolPageControl?.bindElement(bi);
			toolPageControl.data('email', "{path:'userAdminModel>email',mode:'OneWay'}")
		}
		
		
		(sap.ui.getCore().byId("container-ui5.gebit.app---app--toolHeaderImage") as Control).setVisible(true);
		(sap.ui.getCore().byId("container-ui5.gebit.app---app--toolHeaderSandwichIcon-img") as Control)?.setVisible(true);
		
	}

	public isAdmin(role:string) {
		return role === 'admin';
	}

	public navigateToTerritoriesApp(oEven:any) {
		(this.getOwnerComponent() as UIComponent).getRouter().navTo("territories");
	}

	
	public navigateToMyTerritoriesApp(oEven:any) {
		(this.getOwnerComponent() as UIComponent).getRouter().navTo("myTerritories");
	}

	public navigateToGroupTerritoriesApp(oEven:any) {
		(this.getOwnerComponent() as UIComponent).getRouter().navTo("groupTerritories");
	}

	public navigateToSitesApp(oEven:any) {
		(this.getOwnerComponent() as UIComponent).getRouter().navTo("sites");
	}

	public navigateToUserAdminApp(oEven:any) {
		(this.getOwnerComponent() as UIComponent).getRouter().navTo("userAdmin");
	}

	public navigateToVideoTutorialApp(oEven:any) {
		(this.getOwnerComponent() as UIComponent).getRouter().navTo("videoTutorial");
	}

	public navigateToGlobalAdminConsole(oEven:any) {
		(this.getOwnerComponent() as UIComponent).getRouter().navTo("admin");
	}
}