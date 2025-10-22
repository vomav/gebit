import Controller from "sap/ui/core/mvc/Controller";
import UIComponent from "sap/ui/core/UIComponent";
import { Router$RouteMatchedEvent } from "sap/ui/core/routing/Router";
import Event from "sap/ui/base/Event";
import JSONModel from "sap/ui/model/json/JSONModel";
import ODataModel from "sap/ui/model/odata/v4/ODataModel";
import Dialog from "sap/m/Dialog";
import MessageBox from "sap/m/MessageBox";
import MessageToast from "sap/m/MessageToast";
import Context from "sap/ui/model/odata/v4/Context";
import Input from "sap/m/Input";
import View from "sap/ui/core/mvc/View";
import {ObjectBindingInfo} from "sap/ui/base/ManagedObject";
import MapContainer from "../control/MapContainer";
/**
 * @namespace ui5.gebit.app.controller
 */           
export default class TerritoryDetail extends Controller {

	assigTerritoryToUserDialog:Dialog;
	trasferTerritoryToAnotherSiteDialog:Dialog;
	assigTerritoryToUnregisteredUserDialog:Dialog;
	public onInit() : void {
		let router = (this.getOwnerComponent() as UIComponent).getRouter();
		router.attachRouteMatched(this.attachRouteMatched, this);
	}

	public attachRouteMatched(oEvent:Router$RouteMatchedEvent) {
		let routeName = oEvent.getParameter("name");
		if(routeName == "territoryDetail") {
			this.matched(oEvent.getParameter("arguments").id);
		}
	}

	public matched(context:string) {
		let bindingPath = "/Territories(" + context + ")";
		let oView = this.getView() as View;
		let oBindingInfo = {} as ObjectBindingInfo;
		oBindingInfo.path = bindingPath;

		this.getView()?.getModel()?.attachEventOnce("dataReceived", function() {
			console.log("Data received event once");
			let oMapContainer = this.getView()?.byId("mainMapContainer") as MapContainer;
			let rootBindingObject = this.getView()?.getBindingContext()?.getObject();
			rootBindingObject.toParts.forEach(function(part:any) {
				console.log("Adding polygon", part);
				oMapContainer.addPolygon(part.name, part.coordinates);
			}.bind(this));
			// if(this.getView()?.byId("mainMapContainer"))
			// this.getView().byId("mainMapContainer").addPolygon();
			oView.setBusy(false);
		}.bind(this));
		oView.setBusy(true);
		oView.bindElement(oBindingInfo);
	}

	public onEditButton(oEvent:Event) {
		(this.getView()?.getModel("uiModel") as JSONModel).setProperty("/ui/editMode", true);
	}

	public onDisplayMode(oEvent:Event) {
		(this.getView()?.getModel("uiModel") as JSONModel).setProperty("/ui/editMode", false);
	}

	public onDeleteTerritory(oEvent:Event) {
		let model = (this.getView()?.getModel() as ODataModel);

		let bindingPath = this.getView().getBindingContext().getPath();
		if(bindingPath) 
			model.delete(bindingPath);

		(this.getOwnerComponent() as UIComponent).getRouter().navTo("territories");
	}

	public onOpenDialogAssignTerritoryToUserPress (oEvent:Event) {

		let oView = this.getView();
		let that = this;
		if (!this.assigTerritoryToUserDialog) {
			this.loadFragment({name:"ui5.gebit.app.fragment.AssignTerritoryToUser", addToDependents: true}).then(function(dialog:any){
				that.assigTerritoryToUserDialog = dialog as Dialog;
				oView?.addDependent(that.assigTerritoryToUserDialog);
				that.assigTerritoryToUserDialog.bindElement({
					path: "/territory/assignTerritoryToUser",
					model:"uiModel"
				});
				that.assigTerritoryToUserDialog.open();
				return that.assigTerritoryToUserDialog;
			});
		} else {
			that.assigTerritoryToUserDialog.open();
		}
	}

	public onValueHelpClose (oEvent:Event) {
		(this.getView()?.getModel("uiModel") as JSONModel).setProperty("/territory/assignTerritoryToUser", { 
			input : "",
            selectedUser : {}
		});
	}

	public onValueHelpSearch(oEvent:any) {

	} 

	public async onSelectUser(oEvent:any) {
		let userId = oEvent.getParameter("selectedItem").getBindingContext().getObject().user_ID;

		let model = (this.getView()?.getModel() as ODataModel);
		let detailPageContext = this.getView()?.getBindingContext() as Context;
		let context = await model.bindContext("srv.searching.assignToUser(...)", detailPageContext);
		context.setParameter("userId", userId);

		context.execute().then(function () {
			MessageToast.show("{i18n>ok}");
			detailPageContext.refresh();
		}.bind(this), function (oError) {
			MessageBox.error(oError.message);
		}
	);

	}

	public async onWithdraw(oEvent:Event) {
		let model = (this.getView()?.getModel() as ODataModel);
		let detailPageContext = this.getView()?.getBindingContext() as Context;
		let context = await model.bindContext("srv.searching.withdrawFromUser(...)", detailPageContext);

		context.execute().then(function () {
			MessageToast.show("{i18n>ok}");
			detailPageContext.refresh();
		}.bind(this), function (oError) {
			MessageBox.error(oError.message);
		}
	);
	}

	public onPressTransferToAnotherSiteButton(oEvent: Event) {

		let oView = this.getView();
		let that = this;
		if (!this.trasferTerritoryToAnotherSiteDialog) {
			this.loadFragment({name:"ui5.gebit.app.fragment.SitesToSelectWheretoTransfertTerritory", addToDependents: true}).then(function(dialog:any){
				that.trasferTerritoryToAnotherSiteDialog = dialog as Dialog;
				oView?.addDependent(that.trasferTerritoryToAnotherSiteDialog);
				
				that.trasferTerritoryToAnotherSiteDialog.open();
				return that.trasferTerritoryToAnotherSiteDialog;
			});
		} else {
			that.trasferTerritoryToAnotherSiteDialog.open();
		}
	}

	public async onSelectTenant(oEvent:Event) {
		let userId = oEvent.getParameter("selectedItem").getBindingContext().getObject().ID;

		let model = (this.getView()?.getModel() as ODataModel);
		let detailPageContext = this.getView()?.getBindingContext() as Context;
		let context = await model.bindContext("srv.searching.trasferToAnotherSite(...)", detailPageContext);
		context.setParameter("siteId", userId);

		context.execute().then(function () {
				MessageToast.show("{i18n>ok}");
				this.getView()?.getModel().refresh();
			}.bind(this), function (oError) {
				MessageBox.error(oError.message);
			}
		);
	}

	public async onOpenDialogAssignTerritoryToUnassignedUserPress (oEvent:Event) {
		let oView = this.getView();
		let that = this;
		if (!this.assigTerritoryToUnregisteredUserDialog) {
			this.loadFragment({name:"ui5.gebit.app.fragment.AssignTerritoryToUnregisteredUser", addToDependents: true}).then(function(dialog:any){
				that.assigTerritoryToUnregisteredUserDialog = dialog as Dialog;
				oView?.addDependent(that.assigTerritoryToUnregisteredUserDialog);
				that.assigTerritoryToUnregisteredUserDialog.open();
				return that.assigTerritoryToUnregisteredUserDialog;
			});
		} else {
			that.assigTerritoryToUnregisteredUserDialog.open();
		}
	}

	public async onCloseDialogAssignTerritoryToUnassignedUserPress (oEvent:Event) {
		this.assigTerritoryToUnregisteredUserDialog.close();
	}

	public async onAssignUnregisteredUser(oEvent:Event) {
		let username = (this.getView()?.byId("territoryAssignUnregisterUsername") as Input).getValue();
		let email = (this.getView()?.byId("territoryAssignUnregisterEmail") as Input).getValue();;
		let model = (this.getView()?.getModel() as ODataModel);
		let detailPageContext = this.getView()?.getBindingContext() as Context;
		let context = await model.bindContext("srv.searching.assignToUnregisteredUser(...)", detailPageContext);
		context.setParameter("username", username);
		context.setParameter("email", email);

		context.execute().then(function () {
				MessageToast.show("{i18n>ok}");
				this.getView()?.getModel().refresh();
				this.assigTerritoryToUnregisteredUserDialog.close();
			}.bind(this), function (oError) {
				MessageBox.error(oError.message);
				this.assigTerritoryToUnregisteredUserDialog.close();
			}
		);
	}

	public formatPublicTerritoryLink(value:string) {
		return `${window.location.origin}/#/publicSearching/${value}`;
	}

	public onAfterRendering(): void | undefined {
		 
	}
}