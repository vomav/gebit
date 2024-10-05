import MessageBox from "sap/m/MessageBox";
import Controller from "sap/ui/core/mvc/Controller";
import AppComponent from "../Component";

/**
 * @namespace ui5.gebit.app.controller
 */
export default class Detail extends Controller {

	public onInit() : void {
	}

    public navToHome(oEvent:any) {
        (this.getOwnerComponent() as AppComponent).getRouter().navTo("home");
    }
}