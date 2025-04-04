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
import ODataListBinding from "sap/ui/model/odata/v4/ODataListBinding";
import Table from "sap/m/Table";
import Image from "sap/m/Image";
import Toolbar from "sap/m/Toolbar";
import ToolbarSpacer from "sap/m/ToolbarSpacer";
import CustomData from "sap/ui/core/CustomData";
import ContextBinding from "sap/ui/model/ContextBinding";
/**
 * @namespace ui5.gebit.app.controller
 */
export default class GroupTerritoryDetail extends Controller {

	usersDialog: Dialog;
	currentPartsContextBinding: Context;
	viewMapSnapshotDialog: Dialog;
	public onInit(): void {
		let router = (this.getOwnerComponent() as UIComponent).getRouter();
		router.attachRouteMatched(this.attachRouteMatched, this);
	}

	public attachRouteMatched(oEvent: Router$RouteMatchedEvent) {
		let routeName = oEvent.getParameter("name");
		if (routeName == "groupTerritoryDetail") {
			this.matched(oEvent.getParameter("arguments").id);
		}
	}

 	public matched(context: string) {
		(this.getView() as any).bindElement("/PublicTerritoryAssignments(" + context + ")");

	}

	public onActionButtonPress(oEvent: Event) {
		let oButton = oEvent.getSource() as Button;
		this.currentPartsContextBinding = oButton.getBindingContext() ? oButton.getBindingContext():null;
		let i18nModel = this.getView()?.getModel("i18n");
		let actionSheet = oButton.getDependents()[0] as ActionSheet;
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
			}.bind(this), function (oError) {
				MessageBox.error(oError.message);
			}
			);
		}
	}

	public onValueHelpClose(oEvent: Event) {
		this.usersDialog.clone();
	}

	public async onFilterSelect (oEvent:Event) {
		
		let sKey = oEvent.getParameter("key");
		let aFilters = [];
		let oBinding = this.getView()?.byId("groupTerrirtoryParts")?.getBinding("items") as ODataListBinding;
		let isNotDone = new Filter([new Filter("isDone", FilterOperator.EQ, null), new Filter("isDone", FilterOperator.EQ, false)], false);
		switch (sKey) {
			case "All":
			case "CloseToMe":
				break;
			
			case "Available":
				let inWorkByFilterEqNull = new Filter({'path':'inWorkBy', 'operator': FilterOperator.All, 'variable': 'item', 'condition': new Filter({
					'path':'item/id',
					'operator': FilterOperator.EQ,
					'value1': null
				})})
				aFilters.push(new Filter([ inWorkByFilterEqNull, isNotDone], true));
				break;
	
			case "InProgress": 
				let inWorkByFilter = new Filter({'path':'inWorkBy', 'operator': FilterOperator.Any, 'variable': 'item', 'condition': new Filter({
					'path':'item/id',
					'operator': FilterOperator.NE,
					'value1': null
				})})
				let isAssigned = new Filter([inWorkByFilter, isNotDone], true);
				aFilters.push(isAssigned);
				break;
			default:
				break;
		}

		oBinding.filter(new Filter(aFilters));
		oBinding.sort([new Sorter("name", false)]);
		


	}

	public formatLinkToEmbedHtml(link:string) {
		let escapedLink = link.replace("&", "&amp;")
		// return "&lt;iframe src=&quot;" + escapedLink + "&quot width=&quot;100%&quot; height=&quot;480&quot;&gt;&lt;/iframe&gt;"
		return "<iframe src=\"" +link+ "\" width=\"100%\" height=\"480\"></iframe>"
	}

	public async onUpdateMultiValueUpdate(oEvent: Event) {
		let type = oEvent.getParameter("type");
		let source = oEvent.getSource().getBindingContext();

		if(type == "removed") {
			let removedTokens = oEvent.getParameter("removedTokens") as [];
			for(let token of removedTokens) {
				let context = token.getBindingContext();
				await context.delete();
				source.refresh();
			}
		}
	}

	public onFileChange(oEvent: Event) {
		var file = oEvent.getParameters("files").files[0];
		//Upload image
		var reader = new FileReader();
		reader.onload = function (oReaderEvent) {
			// get an access to the content of the file
			let content = oReaderEvent.currentTarget.result;
			this.createFile(file, content, oEvent.getSource().getBindingContext());
		}.bind(this);
		reader.readAsDataURL(file);
	}

	public async createFile(file:any, content:any, bindingContext: Context) {
		var odataModel = this.getOwnerComponent().getModel();
	
		let context = await odataModel.bindContext("srv.searching.uploadImage(...)", bindingContext);
		context.setParameter("file", content);
		context.setParameter("mediaType", file.type);

		context.execute().then(function () {
			MessageToast.show("{i18n>ok}");
			this.getView()?.getModel().refresh();
		}.bind(this), function (oError) {
			MessageBox.error(oError.message);
		}
		);
	}

	public onPressViewWorkedImage(oEvent:Event) {
		if(this.viewMapSnapshotDialog == undefined) {
			this.viewMapSnapshotDialog = new Dialog({
				showHeader: true,
				customHeader: new Toolbar({
					content:[
						new ToolbarSpacer(),
						new Button({
							icon: "sap-icon://decline",
							press: this.closeViewImageDialog.bind(this)
						})
					]
				})
			});
		}

		let image = new Image({
			src:oEvent.getSource().data("imageUrl"),
			width: window.screen.width - 30 + "px",
			height: window.screen.height - 30 + "px"
		});

		this.viewMapSnapshotDialog.removeAllContent();
		this.viewMapSnapshotDialog.addContent(image);
		this.viewMapSnapshotDialog.open();
	}

	public closeViewImageDialog(oEvent: Event) {
		this.viewMapSnapshotDialog.close();
	}
}