import MessageBox from "sap/m/MessageBox";
import Controller from "sap/ui/core/mvc/Controller";
import AppComponent from "../Component";
import UIComponent from "sap/ui/core/UIComponent";
import { Router$RouteMatchedEvent } from "sap/ui/core/routing/Router";
import Component from "../Component";
import Control from "sap/ui/core/Control";
import { ObjectBindingInfo } from "sap/ui/base/ManagedObject";
import { URLHelper } from "sap/m/library";
import Event from "sap/ui/base/Event";
import ODataModel from "sap/ui/model/odata/v4/ODataModel";
import Input from "sap/m/Input";
import MessageToast from "sap/m/MessageToast";
import Button from "sap/m/Button";
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

	public liveChangeConfirmNewPassword(oEvent:Event) {
		let newPassword = this.getView()?.byId("newPassword") as Input;
		let confirmNewPassword = this.getView()?.byId("confirmNewPassword") as Input;
		let changePasswordButton = this.getView()?.byId("changePasswordButton") as Button;
		if(newPassword.getValue() != confirmNewPassword.getValue()) {
			changePasswordButton.setEnabled(false);
			confirmNewPassword.setValueState("Error");
			confirmNewPassword.setValueStateText(this.getView()?.getModel("i18n").getProperty("passwordsDoNotMatch"));
		} else {
			changePasswordButton.setEnabled(true);
			confirmNewPassword.setValueState("None");
			confirmNewPassword.setValueStateText("");
		}
	}

	public async changePassword(oEvent:Event) {
		let model = (this.getView()?.getModel("userAdminModel")) as ODataModel
		let actionContext = await model.bindContext("srv.admin.changePassword(...)", this.getView()?.getBindingContext("userAdminModel"));
		let oldPassword = this.getView()?.byId("oldPassword") as Input;
		let newPassword = this.getView()?.byId("newPassword") as Input;
		let confirmNewPassword = this.getView()?.byId("confirmNewPassword") as Input;
		let changePasswordButton = this.getView()?.byId("changePasswordButton") as Button;
		actionContext.setParameter("newPassword", newPassword.getValue());
		actionContext.setParameter("oldPassword", oldPassword.getValue());
		actionContext.execute().then(function () {
			MessageToast.show("{i18n>ok}");
			this.getView()?.getModel().refresh();
			newPassword.setValue("");
			oldPassword.setValue("");
			confirmNewPassword.setValue("");
			changePasswordButton.setEnabled(false);
		}.bind(this), function (oError) {
			MessageBox.error(oError.message);
			newPassword.setValue("");
			oldPassword.setValue("");
			confirmNewPassword.setValue("");
			changePasswordButton.setEnabled(false);
		});
	}
}