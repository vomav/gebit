import Controller from "sap/ui/core/mvc/Controller";
import UIComponent from "sap/ui/core/UIComponent";
import { Router$RouteMatchedEvent } from "sap/ui/core/routing/Router";
import Event from "sap/ui/base/Event";
import ODataModel from "sap/ui/model/odata/v4/ODataModel";
import Dialog from "sap/m/Dialog";
import Filter from "sap/ui/model/Filter";
import FilterOperator from "sap/ui/model/FilterOperator";
import MessageBox from "sap/m/MessageBox";
import Context from "sap/ui/model/odata/v4/Context";
import ActionSheet from "sap/m/ActionSheet";
import Button from "sap/m/Button";
import MessageToast from "sap/m/MessageToast";
import Sorter from "sap/ui/model/Sorter";
/**
 * @namespace ui5.gebit.app.controller
 */
export default class GroupTerritoryDetail extends Controller {

	usersDialog: Dialog;
	currentPartsContextBinding: Context;
	public onInit(): void {
		let router = (this.getOwnerComponent() as UIComponent).getRouter();
		router.attachRouteMatched(this.attachRouteMatched, this);
	}

	public attachRouteMatched(oEvent: Router$RouteMatchedEvent) {
		let routeName = oEvent.getParameter("name");
		if (routeName == "groupTerritoryDetail") {
			this.matched(oEvent.getParameter("arguments").id);
		}

		this.getView()?.byId("groupTerrirtoryParts")?.getBinding("items").sort(this.getSortter());
	}

	private getSortter() {
		var id_sort=new Sorter("isDone");

		id_sort.fnComparator = function(a, b){

		var intA = parseInt(a), intB = parseInt(b);

		if (intA == intB) {

		return 0;

		}

		if (intA < intB) {

		return -1;

		}

		if (intA > intB) {

		return 1;

		}

		return 0;

		};
	}
 	public matched(context: string) {
		(this.getView() as any).bindElement("/TerritoryAssignments(" + context + ")");

	}

	public onActionButtonPress(oEvent: Event) {
		let oButton = oEvent.getSource() as Button;
		this.currentPartsContextBinding = oButton.getBindingContext() ? oButton.getBindingContext():null;
		let i18nModel = this.getView()?.getModel("i18n");
		let actionSheet = this.getView()?.byId("myPartsactionSheet") as ActionSheet;
		actionSheet?.setModel(i18nModel, "i18n");
		actionSheet.openBy(oButton);

	}

	public async assignPartToMe(oEvent: Event) {
		let model = (this.getView()?.getModel() as ODataModel);
		if (this.currentPartsContextBinding) {
			let context = await model.bindContext("srv.searching.assignPartToMe(...)", this.currentPartsContextBinding);
			context.execute().then(function () {
				MessageToast.show("{i18n>ok}");
				this.getView()?.getModel().refresh();
			}.bind(this), function (oError) {
				MessageBox.error(oError.message);
			}
			);
		}
	}

	public assignPartToUser(oEvent: Event) {
		let that = this;
		if (this.usersDialog == null) {
			this.loadFragment({ name: "ui5.gebit.app.fragment.AssignPartToUser", addToDependents: true }).then(function (dialog: any) {
				that.usersDialog = dialog as Dialog;
				that.usersDialog.open();
			}.bind(this));
		} else {
			that.usersDialog.open();
		}
	}

	public async cancelPartAssignment(oEvent: Event) {
		let model = (this.getView()?.getModel() as ODataModel);
		let context = await model.bindContext("srv.searching.cancelPartAssignment(...)", this.currentPartsContextBinding);
		context.execute().then(function () {
			MessageToast.show("{i18n>ok}");
			this.getView()?.getModel().refresh();
		}.bind(this), function (oError) {
			MessageBox.error(oError.message);
		}
		);
	}

	public async onSelectUser(oEvent: Event) {
		let userId = oEvent.getParameter("selectedItem").getBindingContext().getObject().user_ID;
		let model = (this.getView()?.getModel() as ODataModel);
		if (this.currentPartsContextBinding) {
			let context = await model.bindContext("srv.searching.assignPartToUser(...)", this.currentPartsContextBinding);
			context.setParameter("userId", userId);
			context.execute().then(function () {
				MessageToast.show("{i18n>ok}");
				this.getView()?.getModel().refresh();
				this.currentPartsContextBinding
			}.bind(this), function (oError) {
				MessageBox.error(oError.message);
			}
			);
		}
	}

	public onValueHelpClose(oEvent: Event) {
		this.usersDialog.clone();
	}
}