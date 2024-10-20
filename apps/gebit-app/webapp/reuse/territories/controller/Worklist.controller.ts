import MessageBox from "sap/m/MessageBox";
import Controller from "sap/ui/core/mvc/Controller";
import AppComponent from "../Component";
import UIComponent from "sap/ui/core/UIComponent";
import ODataModel from "sap/ui/model/odata/v4/ODataModel";
import View from "sap/ui/core/mvc/View";
import Control from "sap/ui/core/Control";
import Dialog from "sap/m/Dialog";
import { ObjectBindingInfo } from "sap/ui/base/ManagedObject";
import JSONModel from "sap/ui/model/json/JSONModel";
import Table from "sap/m/Table";
import ODataListBinding from "sap/ui/model/odata/v4/ODataListBinding";
import Event from "sap/ui/base/Event";
import ColumnListItem from "sap/m/ColumnListItem";

/**
 * @namespace ui5.gebit.app.reuse.territories.controller
 */           
export default class Worklist extends Controller {

	createDialog:Dialog;
	public onInit() : void {
		const view = this.getView() as View
		if (view) {
			view.addStyleClass((this.getOwnerComponent() as AppComponent).getContentDensityClass());
		}
	}

	public onUpdateFinished(oEvent:any) {
		console.log("onUpdateFinished");
	} 

	public openCreateDialog(oEvent:any) {
		let that = this;
		if(this.createDialog == null) {
			this.loadFragment({name:"ui5.gebit.app.reuse.territories.fragment.CreateTerritory", addToDependents: true}).then(function(dialog:any){
				that.createDialog = dialog as Dialog;	
				(that.createDialog.getModel("uiModel") as JSONModel).setProperty("/create", {});
				that.createDialog.bindElement({
					path: "/create",
					model:"uiModel"
				});
				that.createDialog.open();

				
			}.bind(this));
		} else {
			that.createDialog.open();
			(that.createDialog.getModel("uiModel") as JSONModel).setProperty("/create", {});
		}
	}

	public createDialogClose() {
		this.closeCreateDialog();
	}

	public createTerritory() {
		var oList = (this.byId("territoriesTable") as Table)
		let oBinding = oList.getBinding("items") as ODataListBinding;
		let oContext = oBinding.create(this.getView()?.getModel("uiModel")?.getProperty("/create"));
		
			
			if(oContext != undefined) {
				oContext.created().then(function (obj:any) {
					this.closeCreateDialog();
				}.bind(this), function (oError:any) {
					// handle rejection of entity creation; if oError.canceled === true then the transient entity has been deleted
						if (!oError.canceled) {
							throw oError; // unexpected error
						}
				});
		}
	}

	private closeCreateDialog() {
		(this.getView()?.getModel("uiModel") as JSONModel).setProperty("/create", {});
		this.createDialog.close();
	}


	public onPressListItem (oEvent:Event) {
		let oBindingContext = (oEvent.getSource() as ColumnListItem).getBindingContext();
		let router = (this.getOwnerComponent() as UIComponent).getRouter();
		router.navTo("detail",{
			"id" : oBindingContext?.getProperty("ID")
		})
		console.log();
	}
}