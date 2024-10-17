import MessageBox from "sap/m/MessageBox";
import Controller from "sap/ui/core/mvc/Controller";
import AppComponent from "../Component";
import UIComponent from "sap/ui/core/UIComponent";
import ODataModel from "sap/ui/model/odata/v4/ODataModel";
import View from "sap/ui/core/mvc/View";
import { URLHelper } from "sap/m/library";
/**
 * @namespace ui5.gebit.app.reuse.public.controller
 */
export default class Register extends Controller {

    
	public onInit() : void {
		// apply content density mode to root view
		const view = this.getView() as View
		if (view) {
			view.addStyleClass((this.getOwnerComponent() as AppComponent).getContentDensityClass());
		}
	}

	
	onAfterRendering(): void | undefined {
		this.getView()?.byId("emailInput")?.setProperty("value", "");
		this.getView()?.byId("nameInput")?.setProperty("value", "");
		this.getView()?.byId("surnameInput")?.setProperty("value", "");
		this.getView()?.byId("passwordInput")?.setProperty("value", "")
	}

	public  async registerUser() : Promise<void> {
		let model = (this.getOwnerComponent() as UIComponent).getModel() as ODataModel;
		let context = await model.bindContext("/register(...)");
		context.setParameter("email", this.getView()?.byId("emailInput")?.getProperty("value"));
		context.setParameter("name", this.getView()?.byId("nameInput")?.getProperty("value"));
		context.setParameter("surname", this.getView()?.byId("surnameInput")?.getProperty("value"));
		context.setParameter("password", this.getView()?.byId("passwordInput")?.getProperty("value"));

		context.execute().then(function () {
			MessageBox.success("Ok");
		}.bind(this), function (oError) {
			MessageBox.error(oError.message);
		}
	);
	}

	public toToLogin(oEvent:any) {
		URLHelper.redirect("#login");
    }
}