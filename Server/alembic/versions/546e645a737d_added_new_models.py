"""Added new models

Revision ID: 546e645a737d
Revises: eeb1de3c7ce8
Create Date: 2020-04-17 02:03:08.773021

"""
from alembic import op
import sqlalchemy as sa

# revision identifiers, used by Alembic.
revision = '546e645a737d'
down_revision = 'eeb1de3c7ce8'
branch_labels = None
depends_on = None


def upgrade():
    # ### commands auto generated by Alembic - please adjust! ###
    pass
    # op.add_column('achievements', sa.Column('description', sa.String(), nullable=False))
    # op.drop_column('achievements', 'data')
    # op.alter_column('apps', 'photo_id',
    #            existing_type=sa.INTEGER(),
    #            nullable=False)
    # op.alter_column('chats', 'name',
    #            existing_type=sa.VARCHAR(),
    #            nullable=True)
    # op.add_column('photos', sa.Column('created_date', sa.DateTime(), nullable=True))
    # ### end Alembic commands ###


def downgrade():
    # ### commands auto generated by Alembic - please adjust! ###
    op.drop_column('photos', 'created_date')
    op.alter_column('chats', 'name',
                    existing_type=sa.VARCHAR(),
                    nullable=False)
    op.alter_column('apps', 'photo_id',
                    existing_type=sa.INTEGER(),
                    nullable=True)
    op.add_column('achievements', sa.Column('data', sa.BLOB(), nullable=False))
    op.drop_column('achievements', 'description')
    # ### end Alembic commands ###
