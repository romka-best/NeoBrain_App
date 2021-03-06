"""Added new models

Revision ID: ea5978df9677
Revises: 546e645a737d
Create Date: 2020-04-17 02:05:15.932426

"""
from alembic import op
import sqlalchemy as sa

# revision identifiers, used by Alembic.
revision = 'ea5978df9677'
down_revision = '546e645a737d'
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
    # ### end Alembic commands ###


def downgrade():
    # ### commands auto generated by Alembic - please adjust! ###
    op.alter_column('chats', 'name',
                    existing_type=sa.VARCHAR(),
                    nullable=False)
    op.alter_column('apps', 'photo_id',
                    existing_type=sa.INTEGER(),
                    nullable=True)
    op.add_column('achievements', sa.Column('data', sa.BLOB(), nullable=False))
    op.drop_column('achievements', 'description')
    # ### end Alembic commands ###
