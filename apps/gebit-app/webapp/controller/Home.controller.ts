import MessageBox from "sap/m/MessageBox";
import Controller from "sap/ui/core/mvc/Controller";
import AppComponent from "../Component";
import UIComponent from "sap/ui/core/UIComponent";

/**
 * @namespace ui5.gebit.app.controller
 */
export default class Home extends Controller {

	public onInit() : void {
		// apply content density mode to root view

	}

	public navToDetail(oEvent:any) {
		(this.getOwnerComponent() as UIComponent).getRouter().navTo("detail");
	}

	public navToRegister(oEvent:any) {
		(this.getOwnerComponent() as UIComponent).getRouter().navTo("registration");
	}
}