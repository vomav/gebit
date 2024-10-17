import MessageBox from "sap/m/MessageBox";
import Controller from "sap/ui/core/mvc/Controller";
import AppComponent from "../Component";
import UIComponent from "sap/ui/core/UIComponent";
import ODataModel from "sap/ui/model/odata/v4/ODataModel";
import View from "sap/ui/core/mvc/View";
import { URLHelper } from "sap/m/library";
import Control from "sap/ui/core/Control";
import { Router$RouteMatchedEvent } from "sap/ui/core/routing/Router";

/**
 * @namespace ui5.gebit.app.reuse.public.controller
 */           
export default class Welcome extends Controller {

	public onInit() : void {
		// apply content density mode to root view
		const view = this.getView() as View
		if (view) {
			view.addStyleClass((this.getOwnerComponent() as AppComponent).getContentDensityClass());
		}

        let router = (this.getOwnerComponent() as UIComponent).getRouter();
		router.attachRouteMatched(this.attachRouteMatched, this);
	}

    public attachRouteMatched(oEvent:Router$RouteMatchedEvent) {
		let routeName = oEvent.getParameter("name");
		if(routeName == "welcome") {
			this.welcomeMatched();
			(sap.ui.getCore().byId("container-ui5.gebit.app---app--toolHeaderImage") as Control).setVisible(false);
			(sap.ui.getCore().byId("container-ui5.gebit.app---app--toolHeaderSandwichIcon-img") as Control).setVisible(false);
		}

        if(routeName == "welcome/login") {
            ((this.getOwnerComponent() as UIComponent)).getRouter().navTo("login");
        } else if(routeName == "welcome/register") {
            ((this.getOwnerComponent() as UIComponent)).getRouter().navTo("register");
        }

	}

    private welcomeMatched () {
        (sap.ui.getCore().byId("container-ui5.gebit.app---app--toolHeaderImage") as Control).setVisible(false);
        (sap.ui.getCore().byId("container-ui5.gebit.app---app--toolHeaderSandwichIcon-img") as Control).setVisible(false);
    }
    onAfterRendering(): void | undefined {

    }
	public toLogin(oEvent:any) {
		// URLHelper.redirect("#login");
        
        ((this.getOwnerComponent() as UIComponent)).getRouter().navTo("login");
    }

    public toRegister(oEvent:any) {
		((this.getOwnerComponent() as UIComponent)).getRouter().navTo("register");
    }
}