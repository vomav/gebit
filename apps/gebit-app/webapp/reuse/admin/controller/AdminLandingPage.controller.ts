import Controller from "sap/ui/core/mvc/Controller";
import AppComponent from "../Component";

/**
 * @namespace ui5.gebit.app.reuse.admin.controller
 */           
export default class AdminLandingPage extends Controller {

	public navigateToUsersWorklist(oEvent:any) : void {
		let oComponent = this.getOwnerComponent() as AppComponent;
		let oRouter = oComponent.getRouter();
		oRouter.navTo("users");
	}


}